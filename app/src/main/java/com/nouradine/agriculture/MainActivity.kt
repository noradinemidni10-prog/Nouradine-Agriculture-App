package com.nouradine.agriculture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import org.json.JSONObject
import kotlin.concurrent.thread

private val Green=Color(0xFF174D35)
private val Green2=Color(0xFF28734A)
private val Lime=Color(0xFFB7D88A)
private val Gold=Color(0xFFD5A64B)
private val Canvas=Color(0xFFF4F7F1)
private val TextDark=Color(0xFF1B3025)
private val Muted=Color(0xFF718078)
private data class CropInfo(val name:String,val season:String,val soil:String,val water:String,val advice:String)
private val cropData=listOf(
 CropInfo("القمح","الخريف؛ يختلف الحصاد حسب الولاية والصنف","تربة طميية جيدة الصرف","ري تكميلي عند الحاجة، خاصة الاستطالة وطرد السنابل","راقب الصدأ والأعشاب واضبط التسميد بنتائج تحليل التربة."),
 CropInfo("الطماطم","ربيع أو خريف حسب المناخ والزراعة المحمية","تربة خصبة جيدة الصرف","منتظم دون تغريق أو تذبذب حاد","راقب اللفحة والذبابة البيضاء واستعمل شتلات سليمة."),
 CropInfo("البطاطا","خريفية أو ربيعية حسب الارتفاع والمناخ","تربة مفككة جيدة الصرف","انتظام الرطوبة خلال تكوين الدرنات","تجنب التغريق والتخضير وراقب اللفحة."),
 CropInfo("الزيتون","الغرس غالباً في فترة السكون بعد زوال الصقيع","تتحمل أنواعاً متعددة مع تجنب ركود الماء","ري تأسيسي وتكميلي حسب الجفاف والعمر","تقليم متوازن ومراقبة ذبابة الزيتون وتحليل التربة."),
 CropInfo("النخيل","فترات معتدلة مع توفر مصدر ري","تربة عميقة جيدة الصرف","احتياج مرتفع نسبياً ويتغير بالعمر والمناخ","راقب سوسة النخيل والآفات واستشر مختصاً محلياً."),
 CropInfo("الفلفل","الربيع أو الخريف في البيوت المحمية","تربة خصبة جيدة الصرف","ري متقارب ومنتظم","راقب المن والتربس وتجنب الإجهاد المائي.")
)
private data class Tile(val title:String,val detail:String,val icon:ImageVector,val page:String)

class MainActivity:ComponentActivity(){
 private var tts:TextToSpeech?=null
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  tts=TextToSpeech(this){status->if(status==TextToSpeech.SUCCESS)tts?.language=Locale("ar")}
  setContent { NouradineV2(speak={tts?.speak(it,TextToSpeech.QUEUE_FLUSH,null,"nouradine-v2")}) }
 }
 override fun onDestroy(){tts?.stop();tts?.shutdown();super.onDestroy()}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun NouradineV2(speak:(String)->Unit){
 var tab by remember{mutableStateOf("الرئيسية")}
 var page by remember{mutableStateOf("الرئيسية")}
 val context=LocalContext.current
 val prefs=context.getSharedPreferences("nouradine_v2",0)
 var farmName by remember{mutableStateOf(prefs.getString("farm_name","مزرعتي")?:"مزرعتي")}
 var owner by remember{mutableStateOf(prefs.getString("owner","")?:"")}
 val go:(String)->Unit={page=it}
 MaterialTheme(colorScheme=lightColorScheme(primary=Green,secondary=Gold,background=Canvas,surface=Color.White,onBackground=TextDark)){
  Scaffold(containerColor=Canvas,topBar={
   TopAppBar(title={Column{Text("NOURADINE",fontSize=11.sp,color=Green2,fontWeight=FontWeight.Bold,letterSpacing=2.sp);Text(if(page=="الرئيسية")"الزراعة بذكاء" else page,fontWeight=FontWeight.Bold,color=TextDark,fontSize=19.sp)}},
    navigationIcon={Box(Modifier.padding(start=12.dp).size(38.dp).background(Green,CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Default.Eco,null,tint=Color.White)}},
    actions={IconButton(onClick={speak("مرحباً بك في نورالدين للزراعة")}){Icon(Icons.Default.VolumeUp,"تشغيل الصوت",tint=Green)}})
  },bottomBar={
   NavigationBar(containerColor=Color.White){listOf(Triple("الرئيسية",Icons.Default.Home,"الرئيسية"),Triple("المزرعة",Icons.Default.Agriculture,"المزرعة"),Triple("الأدوات",Icons.Default.Calculate,"الأدوات"),Triple("حسابي",Icons.Default.Person,"حسابي")).forEach{(label,icon,target)->
    NavigationBarItem(selected=tab==label,onClick={tab=label;page=target},icon={Icon(icon,null)},label={Text(label,fontSize=11.sp)})
   }}
  }){padding->Box(Modifier.fillMaxSize().padding(padding)){
   when(page){
    "الرئيسية"->Dashboard(go,farmName)
    "المزرعة"->FarmPage(farmName,owner,{farmName=it;prefs.edit().putString("farm_name",it).apply()},{owner=it;prefs.edit().putString("owner",it).apply()})
    "الأدوات"->ToolsHub(go)
    "حسابي"->AccountPage(owner,{owner=it;prefs.edit().putString("owner",it).apply()},speak)
    "المساعد الزراعي"->AssistantPage(speak)
    "موسوعة المحاصيل"->CropLibrary()
    "حاسبة الري"->IrrigationCalculator()
    "حاسبة السماد"->FertilizerCalculator()
    "تحويل الوحدات"->UnitCalculator()
    "الطقس الزراعي"->WeatherScreen()
    "فحص النبات بالصورة"->PlantPhotoScreen()
    "التربة"->SoilGuide()
    "الأسمدة والوقاية"->InputSafety()
    "الاشتراك"->SubscriptionInfo()
    else->Dashboard(go,farmName)
   }
  }}
 }
}

@Composable private fun Dashboard(go:(String)->Unit,farm:String){
 val tiles=listOf(
  Tile("المساعد الزراعي","أسئلة بالصوت والكتابة",Icons.Default.Forum,"المساعد الزراعي"),
  Tile("فحص النبات بالصورة","أرفق صورة وسجّل الأعراض",Icons.Default.PhotoCamera,"فحص النبات بالصورة"),
  Tile("موسوعة المحاصيل","معلومات العناية بالمحاصيل",Icons.Default.MenuBook,"موسوعة المحاصيل"),
  Tile("حاسبة الري","حساب حجم مياه الري",Icons.Default.WaterDrop,"حاسبة الري"),
  Tile("حاسبة السماد","حساب كمية المنتج",Icons.Default.Science,"حاسبة السماد"),
  Tile("الطقس الزراعي","قراءة الطقس لموقع الحقل",Icons.Default.Cloud,"الطقس الزراعي"),
  Tile("التربة","خصائص التربة وتحليلها",Icons.Default.Landscape,"التربة"),
  Tile("الأسمدة والوقاية","الاستخدام الآمن",Icons.Default.HealthAndSafety,"الأسمدة والوقاية")
 )
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  item{Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Green)){Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
   Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("مرحباً بك في",color=Color.White.copy(.8f),fontSize=13.sp);Text("نورالدين للزراعة",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.ExtraBold)};Icon(Icons.Default.EnergySavingsLeaf,null,tint=Lime,modifier=Modifier.size(52.dp))}
   Text("أدوات زراعية عملية لمساعدتك على اتخاذ قرار أفضل في الحقل.",color=Color.White.copy(.9f),lineHeight=23.sp)
   Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Place,null,tint=Lime);Text("  $farm",color=Color.White,fontWeight=FontWeight.SemiBold)}
  }}}
  item{Row(verticalAlignment=Alignment.CenterVertically){Text("خدمات المزرعة",fontSize=20.sp,fontWeight=FontWeight.Bold,color=TextDark);Spacer(Modifier.weight(1f));Text("8 أدوات",fontSize=12.sp,color=Muted)}}
  items(tiles.chunked(2)){pair->Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){pair.forEach{tile->Card(Modifier.weight(1f).height(145.dp).clickable{go(tile.page)},shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxSize().padding(14.dp),verticalArrangement=Arrangement.SpaceBetween){Box(Modifier.size(42.dp).background(Color(0xFFEAF2E8),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){Icon(tile.icon,null,tint=Green,modifier=Modifier.size(23.dp))};Column{Text(tile.title,fontWeight=FontWeight.Bold,color=TextDark,fontSize=14.sp);Text(tile.detail,color=Muted,fontSize=11.sp,lineHeight=16.sp,maxLines=2)}}}};if(pair.size==1)Spacer(Modifier.weight(1f))}}
  item{Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFFE8F0E4))){Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.TipsAndUpdates,null,tint=Green);Spacer(Modifier.width(10.dp));Column{Text("نصيحة مهمة",fontWeight=FontWeight.Bold,color=Green);Text("حلّل التربة قبل تحديد جرعات التسميد، ولا تعتمد جرعة موحدة لكل الحقول.",fontSize=12.sp,color=TextDark)}}}}
 }
}

@Composable private fun FarmPage(farm:String,owner:String,onFarm:(String)->Unit,onOwner:(String)->Unit){
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  Text("ملف المزرعة",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Text("احفظ بيانات مزرعتك على هذا الجهاز.",color=Muted)
  OutlinedTextField(farm,onFarm,label={Text("اسم المزرعة")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  OutlinedTextField(owner,onOwner,label={Text("اسم صاحب المزرعة (اختياري)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  InfoCard("الخصوصية","تُحفظ البيانات محلياً على جهازك في هذه النسخة، ولا تتم مزامنتها مع خادم.")
  InfoCard("التطوير اللاحق","إضافة مساحة القطع ونوع التربة والمحاصيل ومصدر المياه وسجل العمليات الزراعية.")
 }
}
@Composable private fun ToolsHub(go:(String)->Unit){
 val pages=listOf("حاسبة الري","حاسبة السماد","تحويل الوحدات","الطقس الزراعي","موسوعة المحاصيل","التربة","الأسمدة والوقاية")
 Column(Modifier.fillMaxSize().padding(16.dp)){Text("الأدوات الزراعية",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Spacer(Modifier.height(12.dp));pages.forEach{p->Card(Modifier.fillMaxWidth().padding(bottom=9.dp).clickable{go(p)},colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(16.dp)){Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Build,null,tint=Green);Spacer(Modifier.width(12.dp));Text(p,Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Icon(Icons.Default.ChevronLeft,null,tint=Muted)}}}}
}
@Composable private fun AssistantPage(speak:(String)->Unit){
 var question by remember{mutableStateOf("")};var loading by remember{mutableStateOf(false)}
 var responseId by remember{mutableStateOf<String?>(null)}
 val messages=remember{mutableStateListOf<Pair<Boolean,String>>() }
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){r->r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let{question=it}}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("مساعد نورالدين",fontSize=22.sp,fontWeight=FontWeight.Bold,color=Green);Text("محادثة زراعية ذكية",fontSize=12.sp,color=Muted)};TextButton(onClick={messages.clear();responseId=null}){Text("محادثة جديدة")}}
  if(BuildConfig.ASSISTANT_API_BASE_URL.isBlank()){
   InfoCard("المساعد الذكي غير موصول بعد","تم تجهيز واجهة الربط الآمن، لكن يجب نشر خادم backend وضبط عنوانه قبل أن تصل الأسئلة إلى نموذج الذكاء الاصطناعي. لن أعرض إجابات محلية على أنها ذكاء اصطناعي.")
  }
  LazyColumn(Modifier.weight(1f,true),verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(vertical=6.dp)){
   if(messages.isEmpty()) item { InfoCard("ابدأ سؤالك","مثال: أوراق الطماطم تلتف إلى الأعلى مع بقع صفراء. ما المعلومات التي تحتاجها لتحديد الاحتمالات؟") }
   items(messages){item->Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=if(item.first)Color(0xFFE7F0E5) else Color.White)){Column(Modifier.padding(13.dp)){Text(if(item.first)"أنت" else "نورالدين",fontSize=11.sp,fontWeight=FontWeight.Bold,color=Green);Spacer(Modifier.height(4.dp));Text(item.second,color=TextDark,lineHeight=23.sp);if(!item.first)TextButton(onClick={speak(item.second)}){Icon(Icons.Default.VolumeUp,null);Text("استمع")}}} }
   if(loading)item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.Center){CircularProgressIndicator(Modifier.size(24.dp),strokeWidth=2.dp);Text("  نورالدين يكتب الإجابة...",color=Muted)} }
  }
  OutlinedTextField(question,{question=it},modifier=Modifier.fillMaxWidth(),label={Text("اكتب سؤالك الزراعي...")},minLines=2,maxLines=5)
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
   OutlinedButton(enabled=!loading,onClick={val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar-DZ")};try{launcher.launch(intent)}catch(_:Exception){messages.add(false,"التعرف الصوتي غير متاح على هذا الجهاز.")}},modifier=Modifier.weight(0.8f)){Icon(Icons.Default.Mic,null);Text("صوت")}
   Button(enabled=!loading&&question.isNotBlank()&&BuildConfig.ASSISTANT_API_BASE_URL.isNotBlank(),onClick={
    val prompt=question.trim();question="";messages.add(true,prompt);loading=true
    thread {
     var resultText:String
     var newId:String?=null
     try {
      val base=BuildConfig.ASSISTANT_API_BASE_URL.trimEnd('/')
      val connection=(URL("$base/v1/assistant/respond").openConnection() as HttpURLConnection).apply{requestMethod="POST";connectTimeout=10000;readTimeout=60000;doOutput=true;setRequestProperty("Content-Type","application/json; charset=utf-8");setRequestProperty("Accept","application/json")}
      val payload=JSONObject().put("message",prompt);responseId?.let{payload.put("previous_response_id",it)}
      connection.outputStream.use{it.write(payload.toString().toByteArray(Charsets.UTF_8))}
      val code=connection.responseCode;val stream=if(code in 200..299)connection.inputStream else connection.errorStream
      val body=stream?.bufferedReader()?.use{it.readText()}?: "{}";connection.disconnect()
      val json=JSONObject(body);if(code !in 200..299)throw IllegalStateException(json.optString("error","تعذر الاتصال بالخادم (HTTP $code)."))
      resultText=json.optString("answer","").ifBlank{throw IllegalStateException("وصل رد فارغ من الخادم.")};newId=json.optString("response_id").takeIf{it.isNotBlank()}
     } catch(e:Exception){resultText="تعذر الحصول على إجابة: ${e.message?: "تحقق من الإنترنت وإعداد الخادم."}"}
     android.os.Handler(android.os.Looper.getMainLooper()).post{if(newId!=null)responseId=newId;messages.add(false,resultText);loading=false}
    }
   },modifier=Modifier.weight(1.2f)){Icon(Icons.Default.Send,null);Text("إرسال")}
  }
  Text("الإجابات الذكية تتطلب اتصال الإنترنت وخادم التطبيق المنشور.",fontSize=11.sp,color=Muted)
 }
}
private fun answerFor(raw:String):String{val q=raw.trim();return when{
 q.isBlank()->"اكتب سؤالك أولاً."
 q.contains("طماطم")||q.contains("بندورة")->"للطماطم: حافظ على رطوبة منتظمة دون تغريق، وراقب اللفحة والذبابة البيضاء. اذكر الأعراض وعمر النبات والمنطقة لتضييق الاحتمالات."
 q.contains("قمح")->"في القمح راقب الرطوبة خلال الاستطالة وطرد السنابل، وافحص الصدأ والأعشاب. حدد التسميد بتحليل التربة ومرحلة النمو."
 q.contains("اصفرار")||q.contains("أصفر")->"الاصفرار قد ينتج عن نقص عنصر أو اختناق الجذور أو الملوحة أو مرض. افحص توزيع الاصفرار ورطوبة التربة والجذور قبل إضافة السماد."
 q.contains("ري")||q.contains("ماء")->"حجم الماء النظري يعتمد على المساحة وعمق الري. راعِ كفاءة الشبكة والمطر الفعال ونوع التربة وعمق الجذور."
 q.contains("سماد")->"لا تستخدم جرعة عشوائية. اعتمد تحليل التربة وتوصية مختص، ثم استخدم حاسبة السماد لحساب وزن المنتج للمساحة."
 else->"أحتاج اسم المحصول والمنطقة وعمر النبات ووصف الأعراض ونظام الري. لا أستطيع تأكيد تشخيص أو جرعة من هذه المعلومات وحدها."
}}

@Composable private fun CropLibrary(){
 var search by remember{mutableStateOf("")};var selected by remember{mutableStateOf<CropInfo?>(null)}
 Column(Modifier.fillMaxSize().padding(16.dp)){Text("موسوعة المحاصيل",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);OutlinedTextField(search,{search=it},modifier=Modifier.fillMaxWidth(),label={Text("ابحث عن محصول")},singleLine=true)
 if(selected==null)LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(vertical=10.dp)){items(cropData.filter{it.name.contains(search,true)}){c->Card(Modifier.fillMaxWidth().clickable{selected=c},colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Spa,null,tint=Green);Spacer(Modifier.width(12.dp));Text(c.name,Modifier.weight(1f),fontWeight=FontWeight.Bold);Icon(Icons.Default.ChevronLeft,null)}}}}
 else{TextButton(onClick={selected=null}){Text("رجوع")};Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(9.dp)){Text(selected!!.name,fontSize=22.sp,fontWeight=FontWeight.Bold,color=Green);InfoCard("الموسم",selected!!.season);InfoCard("التربة",selected!!.soil);InfoCard("الري",selected!!.water);InfoCard("العناية",selected!!.advice);InfoCard("ملاحظة","تختلف التوصيات باختلاف الولاية والصنف وتاريخ الزراعة والتحليل المخبري.")}}
 }
}
@Composable private fun IrrigationCalculator(){
 var area by remember{mutableStateOf("1")};var depth by remember{mutableStateOf("10")};var efficiency by remember{mutableStateOf("85")};var result by remember{mutableStateOf("")}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("حاسبة مياه الري",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Text("حجم نظري ثم تقدير كمية الضخ بعد إدخال كفاءة الشبكة.",color=Muted)
  NumberField("المساحة (هكتار)",area){area=it};NumberField("عمق الري الصافي (ملم)",depth){depth=it};NumberField("كفاءة شبكة الري (%)",efficiency){efficiency=it}
  Button(onClick={val a=number(area);val d=number(depth);val e=number(efficiency);result=if(a!=null&&d!=null&&e!=null&&a>0&&d>0&&e in 1.0..100.0){val net=a*d*10;val gross=net/(e/100); "الاحتياج الصافي: %.1f م³\nالكمية التقريبية عند الشبكة: %.1f م³".format(net,gross)}else"تحقق من القيم: المساحة والعمق أكبر من صفر والكفاءة بين 1 و100%."},modifier=Modifier.fillMaxWidth()){Text("احسب حجم المياه")}
  if(result.isNotBlank())InfoCard("النتيجة",result)
  InfoCard("حدود الحساب","هذا تقدير حسابي وليس جدول ري. لا يخصم المطر الفعال ولا يحدد توقيت الري أو عمق الجذور.")
 }
}
@Composable private fun FertilizerCalculator(){
 var area by remember{mutableStateOf("1")};var rate by remember{mutableStateOf("60")};var bag by remember{mutableStateOf("50")};var result by remember{mutableStateOf("")}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("حاسبة كمية السماد",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Text("أدخل توصية السماد من تحليل التربة أو المختص. التطبيق لا يخترع جرعة.",color=Muted)
  NumberField("المساحة (هكتار)",area){area=it};NumberField("الجرعة المعتمدة (كغ منتج/هكتار)",rate){rate=it};NumberField("وزن الكيس (كغ)",bag){bag=it}
  Button(onClick={val a=number(area);val r=number(rate);val b=number(bag);result=if(a!=null&&r!=null&&b!=null&&a>0&&r>0&&b>0){val kg=a*r;"إجمالي المنتج: %.2f كغ\nعدد الأكياس: %d كيساً".format(kg,kotlin.math.ceil(kg/b).toInt())}else"أدخل قيماً أكبر من الصفر."},modifier=Modifier.fillMaxWidth()){Text("احسب الكمية")}
  if(result.isNotBlank())InfoCard("النتيجة",result)
  InfoCard("تحذير","لا تخلط بين كغ المنتج وكغ العنصر الغذائي. تحقق من تركيز السماد وطريقة التطبيق والتوصية الزراعية.")
 }
}
@Composable private fun UnitCalculator(){
 var hectares by remember{mutableStateOf("1")};var output by remember{mutableStateOf("")}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("تحويل المساحات",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);NumberField("المساحة بالهكتار",hectares){hectares=it};Button(onClick={val a=number(hectares);output=if(a!=null&&a>0)"%.3f هكتار = %.1f متر مربع = %.2f كيلومتر مربع".format(a,a*10000,a/100)else"أدخل مساحة صحيحة أكبر من الصفر."},modifier=Modifier.fillMaxWidth()){Text("تحويل")};if(output.isNotBlank())InfoCard("النتيجة",output);InfoCard("ثوابت التحويل","1 هكتار = 10,000 متر مربع. 1 كيلومتر مربع = 100 هكتار.")}
}
@Composable private fun WeatherScreen(){
 var lat by remember{mutableStateOf("35.55")};var lon by remember{mutableStateOf("6.17")};var status by remember{mutableStateOf("إحداثيات افتراضية تقريبية لباتنة؛ غيّرها إلى موقع حقلك.")};var loading by remember{mutableStateOf(false)}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("الطقس الزراعي",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Text("بيانات الطقس الحالية من Open-Meteo عبر الإنترنت.",color=Muted)
  NumberField("خط العرض",lat){lat=it};NumberField("خط الطول",lon){lon=it}
  Button(enabled=!loading,onClick={val a=number(lat);val b=number(lon);if(a==null||b==null||a !in -90.0..90.0||b !in -180.0..180.0)status="الإحداثيات غير صحيحة." else{loading=true;status="جارٍ الاتصال بمصدر الطقس...";thread{try{val url=URL("https://api.open-meteo.com/v1/forecast?latitude=$a&longitude=$b&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&timezone=auto");val c=url.openConnection() as HttpURLConnection;c.connectTimeout=12000;c.readTimeout=12000;val j=JSONObject(c.inputStream.bufferedReader().use{it.readText()}).getJSONObject("current");val msg="الحرارة: "+j.getDouble("temperature_2m")+" °م\nالرطوبة: "+j.getInt("relative_humidity_2m")+" %\nالهطول: "+j.getDouble("precipitation")+" ملم\nالرياح: "+j.getDouble("wind_speed_10m")+" كم/س";c.disconnect();android.os.Handler(android.os.Looper.getMainLooper()).post{status=msg;loading=false}}catch(_:Exception){android.os.Handler(android.os.Looper.getMainLooper()).post{status="تعذر جلب البيانات. تحقق من الإنترنت والإحداثيات.";loading=false}}}}},modifier=Modifier.fillMaxWidth()){Text(if(loading)"جارٍ التحميل..." else "تحديث الطقس")}
  InfoCard("القراءة الحالية",status);InfoCard("تنبيه","ليست هذه توقعات زراعية متخصصة. لا تعتمد عليها وحدها في قرارات الرش أو الري الحساسة.")
 }
}
@Composable private fun PlantPhotoScreen(){
 var uri by remember{mutableStateOf<Uri?>(null)};var crop by remember{mutableStateOf("")};var symptoms by remember{mutableStateOf("")}
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri=it}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("فحص النبات بالصورة",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);Text("اختر صورة واضحة وسجل بيانات الحالة لتحضيرها للفحص أو عرضها على مهندس زراعي.",color=Muted)
  Button(onClick={picker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.AddPhotoAlternate,null);Text("اختيار صورة من الهاتف")}
  uri?.let{u->AndroidView(factory={ctx->ImageView(ctx).apply{setImageURI(u);scaleType=ImageView.ScaleType.FIT_CENTER}},modifier=Modifier.fillMaxWidth().height(240.dp))}
  OutlinedTextField(crop,{crop=it},label={Text("اسم المحصول")},modifier=Modifier.fillMaxWidth())
  OutlinedTextField(symptoms,{symptoms=it},label={Text("الأعراض ومتى بدأت")},modifier=Modifier.fillMaxWidth(),minLines=3)
  InfoCard("حالة الميزة","اختيار الصورة وتسجيل الأعراض يعملان محلياً. لا يوجد حتى الآن محرك ذكاء اصطناعي يحلل الصورة؛ لذلك لن أعطيك تشخيصاً وهمياً.")
  InfoCard("لنتيجة أدق","صوّر النبات كاملاً والورقة المصابة وظهر الورقة، واذكر الولاية وعمر النبات والري وأي معاملة حديثة.")
 }
}
@Composable private fun SoilGuide(){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("التربة وتحليل الأراضي",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);InfoCard("رملية","تصريف سريع واحتفاظ أقل بالماء غالباً. أضف مادة عضوية متحللة وأدر الري على دفعات مناسبة.");InfoCard("طينية","احتفاظ مرتفع بالماء واحتمال الانضغاط والتغريق. حسّن الصرف ولا تعمل بالتربة وهي مشبعة.");InfoCard("طميية","خصائص متوازنة نسبياً، لكن الخصوبة والملوحة لا تعرف من الملمس وحده.");InfoCard("تحاليل أساسية","pH وEC والمادة العضوية والآزوت والفوسفور والبوتاسيوم والعناصر الصغرى حسب المحصول.");InfoCard("أخذ العينة","خذ عينات فرعية من مناطق متجانسة وبعمق مناسب للمحصول، واخلطها في عينة ممثلة واتبع تعليمات المخبر.")}}
@Composable private fun InputSafety(){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("الأسمدة والوقاية النباتية",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);InfoCard("التسميد","حدد الجرعة من تحليل التربة واحتياجات المحصول ومرحلة النمو. افصل بين كمية المنتج وكمية العنصر الغذائي.");InfoCard("المبيدات","استخدم منتجاً مسجلاً للمحصول والآفة في بلدك، والتزم بالملصق والجرعة وفترة ما قبل الحصاد.");InfoCard("معدات الوقاية","ارتدِ معدات الوقاية المحددة على الملصق، وتجنب الرياح ومصادر المياه والأطفال والحيوانات.");InfoCard("الخلط","لا تخلط المبيدات أو الأسمدة الورقية دون تأكيد التوافق من الملصق أو مختص.")}}
@Composable private fun SubscriptionInfo(){Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("الاشتراك",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);InfoCard("التجربة","30 يوماً وفق الخطة المقترحة.");InfoCard("السعر المخطط","8 دولارات لكل دورة مدتها شهران. الدفع غير مفعل في هذه النسخة.");InfoCard("العملة الخاصة","مؤجلة إلى ما بعد انتشار التطبيق ونجاحه.")}}
@Composable private fun AccountPage(owner:String,onOwner:(String)->Unit,speak:(String)->Unit){
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Box(Modifier.size(82.dp).background(Color(0xFFE5EFE3),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Default.Person,null,tint=Green,modifier=Modifier.size(42.dp))}
  Text("حسابي",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Green);OutlinedTextField(owner,onOwner,label={Text("اسم المستخدم")},modifier=Modifier.fillMaxWidth())
  OutlinedButton(onClick={speak("مرحباً بك في تطبيق نورالدين للزراعة")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.RecordVoiceOver,null);Text("اختبار الصوت")}
  InfoCard("حالة الحساب","الملف محلي على الجهاز. تسجيل الدخول والمزامنة السحابية غير مفعّلين بعد.")
  InfoCard("الدعم","تواصل مع الدعم الفني عند توفر قناة الدعم الرسمية.")
 }
}
@Composable private fun NumberField(label:String,value:String,onValue:(String)->Unit){OutlinedTextField(value,onValue,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))}
private fun number(s:String)=s.trim().replace(',','.').toDoubleOrNull()?.takeIf{it.isFinite()}
@Composable private fun InfoCard(title:String,body:String){Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)){Column(Modifier.fillMaxWidth().padding(15.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){Text(title,fontWeight=FontWeight.Bold,color=Green,fontSize=15.sp);Text(body,color=TextDark,lineHeight=22.sp,fontSize=13.sp)}}}
