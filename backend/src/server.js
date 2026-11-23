import express from "express";
import helmet from "helmet";
import cors from "cors";
import rateLimit from "express-rate-limit";
import OpenAI from "openai";

const app = express();
const port = Number(process.env.PORT || 8080);
const apiKey = process.env.OPENAI_API_KEY;
const model = process.env.OPENAI_MODEL || "gpt-6.2-astra";
const origins = (process.env.ALLOWED_ORIGINS || "").split(",").map(x => x.trim()).filter(Boolean);

app.disable("x-powered-by");
app.use(helmet());
app.use(cors({ origin: origins.length ? origins : false }));
app.use(express.json({ limit: "10mb", strict: true }));
app.use("/v1/assistant", rateLimit({
  windowMs: 60_000,
  limit: 20,
  standardHeaders: "draft-7",
  legacyHeaders: false,
  message: { error: "تم تجاوز عدد الطلبات مؤقتاً. حاول بعد دقيقة." }
}));

const client = apiKey ? new OpenAI({ apiKey, timeout: 45_000, maxRetries: 1 }) : null;
const instructions = `أنت نورالدين، مساعد زراعي ذكي يتحدث العربية بوضوح ويمكنه فهم العربية الجزائرية والفرنسية والإنجليزية.
ساعد المزارع بإجابات عملية ومختصرة ومنظمة. اسأل عن الولاية والمحصول والصنف وعمر النبات والتربة والري والأعراض عندما تؤثر في دقة النصيحة.
لا تخترع جرعات الأسمدة أو المبيدات ولا تؤكد تشخيص مرض من صورة أو وصف غير كافٍ. وضح الاحتمالات واطلب معلومات إضافية، ووجّه المستخدم إلى ملصق المنتج المسجل والمهندس الزراعي المحلي في القرارات الحساسة.
ميّز بين المعلومة المؤكدة والتقدير. لا تدّع أنك قست التربة أو الطقس أو فحصت مختبرياً. إذا كانت المعلومة متغيرة أو محلية فاذكر ضرورة التحقق من مصدر محلي حديث.`;

app.get("/health", (_req, res) => {
  res.status(200).json({ ok: true, service: "nouradine-agriculture-api", aiConfigured: Boolean(client) });
});

app.post("/v1/assistant/respond", async (req, res) => {
  if (!client) return res.status(503).json({ error: "الخادم لم يُضبط بمفتاح الذكاء الاصطناعي بعد." });
  const message = typeof req.body?.message === "string" ? req.body.message.trim() : "";
  if (!message || message.length > 8_000) {
    return res.status(400).json({ error: "أرسل سؤالاً بين حرف واحد و8000 حرف." });
  }

  const previousResponseId = req.body?.previous_response_id;
  if (previousResponseId !== undefined &&
      (typeof previousResponseId !== "string" || !/^resp_[A-Za-z0-9_-]{8,}$/.test(previousResponseId))) {
    return res.status(400).json({ error: "معرّف المحادثة غير صالح." });
  }

  const content = [{ type: "input_text", text: message }];
  const image = req.body?.image_data_url;
  if (image !== undefined) {
    if (typeof image !== "string" || image.length > 8_000_000 ||
        !/^data:image\/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$/.test(image)) {
      return res.status(400).json({ error: "الصورة غير صالحة أو أكبر من الحد المسموح." });
    }
    content.push({ type: "input_image", image_url: image, detail: "auto" });
  }

  try {
    const response = await client.responses.create({
      model,
      instructions,
      input: [{ role: "user", content }],
      ...(previousResponseId ? { previous_response_id: previousResponseId } : {})
    });
    const answer = response.output_text?.trim();
    if (!answer) return res.status(502).json({ error: "لم يرجع النموذج إجابة نصية. حاول مرة أخرى." });
    return res.status(200).json({ answer, response_id: response.id, model: response.model });
  } catch (error) {
    const status = Number(error?.status);
    if (status === 429) return res.status(429).json({ error: "الخدمة مشغولة حالياً. أعد المحاولة بعد قليل." });
    if (status === 401 || status === 403) {
      console.error("AI provider authentication failed");
      return res.status(503).json({ error: "إعدادات خدمة الذكاء الاصطناعي غير صحيحة على الخادم." });
    }
    console.error("Assistant request failed:", error?.name || "Error", status || "no-status");
    return res.status(502).json({ error: "تعذر الحصول على إجابة الآن. تحقق من الاتصال وحاول مجدداً." });
  }
});

app.listen(port, () => {
  console.log(`Nouradine API listening on port ${port}`);
});
