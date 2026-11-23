package com.nouradine.agriculture
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import org.json.JSONObject

private val Forest=Color(0xFF245B35)
private val Leaf=Color(0xFF4E8B45)
private val Gold=Color(0xFFC69A42)
private val Cream=Color(0xFFF5F7EF)
private val Ink=Color(0xFF203126)
private data class Crop(val name:String,val season:String,val soil:String,val water:String,val care:String)
private val crops=listOf(
 Crop("القمح","الخريف؛ الحصاد في أواخر الربيع أو الصيف حسب المنطقة","تربة طميية جيدة الصرف","ري تكميلي عند نقص الأمطار، خصوصاً في الاستطالة وطرد السنابل","تجنب التغريق وراقب الصدأ والأعشاب واضبط التسميد بتحليل التربة."),
 Crop("الطماطم","الربيع أو الخريف حسب المناخ ونظام الزراعة","تربة خصبة جيدة الصرف","ري منتظم دون تذبذب حاد","راقب اللفحة والذبابة البيضاء واستعمل شتلات سليمة ودعائم مناسبة."),
 Crop("البطاطا","تختلف حسب الارتفاع والمناخ؛ غالباً خريفية أو ربيعية","تربة مفككة جيدة الصرف","احتياج منتظم خصوصاً أثناء تكوين الدرنات","تجنب تعريض الدرنات للضوء وراقب اللفحة والري الزائد."),
 Crop("الزيتون","الغرس غالباً في موسم السكون بعد زوال خطر الصقيع","تربة جيدة الصرف وتتحمل مجالاً واسعاً من القوام","ري تأسيسي للأشجار الصغيرة وري تكميلي عند الجفاف","تقليم متوازن ومراقبة ذبابة الزيتون وتحليل التربة دورياً."),
 Crop("النخيل","فترات معتدلة الحرارة مع توفر الري","تربة عميقة جيدة الصرف","احتياج مرتفع نسبياً ويتغير مع العمر والمناخ","راقب سوسة النخيل الحمراء والآفات ولا تعتمد جرعة سماد موحدة."),
 Crop("الفلفل","الربيع أو الخريف في البيوت المحمية","تربة خصبة جيدة الصرف","ري متقارب ومنتظم","تجنب الإجهاد المائي وراقب المن والتربس.")
)
private data class Feature(val title:String,val subtitle:String,val icon:ImageVector)

class MainActivity:ComponentActivity(){
 private var tts:TextToSpeech?=null
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  tts=TextToSpeech(this){status->if(status==TextToSpeech.SUCCESS)tts?.language=Locale("ar")}
  setContent{NouradineApp(speak={tts?.speak(it,TextToSpeech.QUEUE_FLUSH,null,"nouradine")})}
 }
 override fun onDestroy(){tts?.stop();tts?.shutdown();super.onDestroy()}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun NouradineApp(speak:(String)->Unit){
 var page by remember{mutableStateOf("الرئيسية")}
 val context=androidx.compose.ui.platform.LocalContext.current
 val prefs=context.getSharedPreferences("nouradine",0)
 var welcome by remember{mutableStateOf(prefs.getBoolean("welcome_voice",true))}
 var spoken by remember{mutableStateOf(false)}
 LaunchedEffect(Unit){if(welcome&&!spoken){speak("مرحباً بك، أنا نورالدين، أهلاً وسهلاً بك في تطبيق نورالدين للزراعة");spoken=true}}
 MaterialTheme(colorScheme=lightColorScheme(primary=Forest,secondary=Gold,background=Cream,surface=Color.White)){
  Scaffold(containerColor=Cream,topBar={TopAppBar(title={Column{Text("Nouradine Agriculture",fontSize=12.sp,color=Leaf);Text(page,fontWeight=FontWeight.Bold,color=Ink)}},navigationIcon={Icon(Icons.Default.Eco,null,tint=Forest,modifier=Modifier.padding(start=12.dp).size(28.dp))},actions={IconButton(onClick={speak("مرحباً بك، أنا نورالدين، أهلاً وسهلاً بك في تطبيق نورالدين للزراعة")}){Icon(Icons.Default.VolumeUp,"تشغيل الترحيب",tint=Forest)}})},
   bottomBar={NavigationBar(containerColor=Color.White){listOf("الرئيسية","المساعد","الأدوات","الإعدادات").forEach{label->val icon=when(label){"الرئيسية"->Icons.Default.Home;"المساعد"->Icons.Default.Chat;"الأدوات"->Icons.Default.Agriculture;else->Icons.Default.Settings};NavigationBarItem(selected=page==label,onClick={page=label},icon={Icon(icon,null)},label={Text(label,fontSize=10.sp)})}}},
   ){pad->Box(Modifier.fillMaxSize().padding(pad)){when(page){
    "الرئيسية"->HomePage{page=it}
    "المساعد"->AssistantPage(speak)
    "الأدوات"->ToolsPage{page=it}
    "الإعدادات","اللغات والصوت"->SettingsPage(welcome){welcome=it;prefs.edit().putBoolean("welcome_voice",it).apply()}
    "تشخيص أمراض النباتات"->DiagnosisPage()
    "موسوعة المحاصيل"->CropsPage()
    "التربة والأراضي"->SoilPage()
    "الأسمدة والمبيدات"->FertilizerPage()
    "الري والتسميد"->IrrigationPage()
    "الطقس والتنبيهات"->WeatherPage()
    "الملف الشخصي والخلفية"->ProfilePage()
    "الاشتراك والتحديثات"->SubscriptionPage()
    "دليل الاتصالات"->CallerInfoPage()
    else->HomePage{page=it}
   }}}
 }
}

@Composable private fun HomePage(go:(String)->Unit){
 val entries=listOf(
  Feature("المساعد الزراعي الذكي","اسأل كتابة أو بالصوت عن الزراعة",Icons.Default.Chat),
  Feature("تشخيص أمراض النباتات","اختر صورة للنبات للفحص",Icons.Default.CameraAlt),
  Feature("موسوعة المحاصيل","دليل المحاصيل والعناية بها",Icons.Default.MenuBook),
  Feature("التربة والأراضي","أنواع التربة وتحسينها",Icons.Default.Landscape),
  Feature("الأسمدة والمبيدات","إرشادات السلامة والاستخدام",Icons.Default.Science),
  Feature("الري والتسميد","حساب أولي لاحتياجات الري",Icons.Default.WaterDrop),
  Feature("الطقس والتنبيهات","الطقس الحالي بإحداثيات الحقل",Icons.Default.Cloud),
  Feature("اللغات والصوت","إعدادات اللغة والترحيب الصوتي",Icons.Default.Language),
  Feature("الملف الشخصي والخلفية","تخصيص الملف والصورة",Icons.Default.Person),
  Feature("الاشتراك والتحديثات","تفاصيل التجربة والاشتراك",Icons.Default.Star),
  Feature("دليل الاتصالات","معلومات تعريف المتصل",Icons.Default.Call)
 )
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(11.dp)){
  item{Card(colors=CardDefaults.cardColors(containerColor=Forest),shape=RoundedCornerShape(24.dp)){Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text("مرحباً بك في عالم الزراعة الذكية",color=Color.White,fontSize=21.sp,fontWeight=FontWeight.Bold);Text("معلومات وأدوات زراعية في مكان واحد، مع مراعاة اختلاف المناخ والتربة وطرق الزراعة.",color=Color(0xFFE5F0E2),lineHeight=23.sp);Text("معاً من أجل محصول أفضل 🌱",color=Gold,fontWeight=FontWeight.Bold)}}}
  item{Text("جميع خدمات نورالدين",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Ink)}
  items(entries){item->Card(Modifier.fillMaxWidth().clickable{go(item.title)},shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){Icon(item.icon,null,tint=Forest,modifier=Modifier.size(30.dp));Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(item.title,fontWeight=FontWeight.Bold,color=Ink);Text(item.subtitle,fontSize=12.sp,color=Color.Gray)};Icon(Icons.Default.ChevronLeft,null,tint=Leaf)}}}
 }
}

@Composable private fun AssistantPage(speak:(String)->Unit){
 var question by remember{mutableStateOf("")}
 var answer by remember{mutableStateOf("أهلاً بك. اكتب سؤالك الزراعي وسأعرض إرشادات أولية من قاعدة المعرفة المحلية.")}
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){r->question=r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Card(colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(16.dp)){Text("المساعد الزراعي نورالدين",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Forest);Text(answer,color=Ink,lineHeight=24.sp);TextButton(onClick={speak(answer)}){Icon(Icons.Default.VolumeUp,null);Text("استمع إلى الإجابة")}}}
  OutlinedTextField(question,{question=it},modifier=Modifier.fillMaxWidth(),label={Text("اكتب سؤالك الزراعي")},minLines=2)
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
   Button(onClick={val q=question.trim();answer=when{
    q.isBlank()->"اكتب سؤالاً أولاً."
    q.contains("طماطم")||q.contains("بندورة")->"للطماطم: وفر تربة جيدة الصرف ورياً منتظماً دون تغريق، وراقب اللفحة والذبابة البيضاء. حدد الأعراض والمنطقة لتوجيه أدق."
    q.contains("قمح")->"القمح يحتاج متابعة الرطوبة خصوصاً الاستطالة وطرد السنابل، مع مراقبة الصدأ والأعشاب. اضبط السماد وفق تحليل التربة."
    q.contains("اصفرار")||q.contains("أصفر")->"الاصفرار قد ينتج عن نقص عناصر أو زيادة ري أو ملوحة أو مرض جذري. افحص نمطه والرطوبة والجذور قبل إضافة السماد."
    q.contains("ري")||q.contains("ماء")->"اضبط الري حسب نوع التربة والطقس وعمر النبات. افحص رطوبة منطقة الجذور وتجنب التغريق؛ لا توجد كمية واحدة تناسب كل الحقول."
    q.contains("سماد")->"لا تضف جرعة عشوائية. اعتمد تحليل التربة واحتياجات المحصول ومرحلة النمو واتبع ملصق المنتج."
    else->"أحتاج تفاصيل أكثر: اسم المحصول، المنطقة، عمر النبات، الأعراض وطريقة الري. هذه إرشادات محلية أولية وليست تشخيصاً آلياً أو إجابة ذكاء اصطناعي متصلة بالإنترنت."
   }},modifier=Modifier.weight(1f)){Icon(Icons.Default.Send,null);Text("إرسال")}
   OutlinedButton(onClick={val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar-DZ");putExtra(RecognizerIntent.EXTRA_PROMPT,"تحدث عن سؤالك الزراعي")};try{launcher.launch(i)}catch(_:Exception){question="ميزة التعرف الصوتي غير متاحة على الجهاز."}}){Icon(Icons.Default.Mic,null)}
  }
  Text("قاعدة إرشادية محلية أولية؛ لم يتم ربط نموذج ذكاء اصطناعي سحابي بعد.",fontSize=12.sp,color=Color.Gray)
 }
}

@Composable private fun DiagnosisPage(){
 var uri by remember{mutableStateOf<Uri?>(null)}
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri=it}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("تشخيص أمراض النباتات بالصور",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  Text("اختر صورة واضحة للأوراق والساق والأعراض. لا يصدر التطبيق تشخيصاً آلياً مؤكداً في هذه المرحلة.")
  Button(onClick={picker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.AddPhotoAlternate,null);Text("اختيار صورة من الهاتف")}
  uri?.let{u->AndroidView(factory={ImageView(it).apply{scaleType=ImageView.ScaleType.FIT_CENTER;setImageURI(u)}},modifier=Modifier.fillMaxWidth().height(260.dp));Text("تم اختيار الصورة. أضف اسم المحصول والمنطقة والأعراض قبل طلب رأي مختص.",color=Ink)}
  InfoBlock("السلامة","لا تستخدم مبيداً اعتماداً على صورة أو تخمين فقط. تحقق من التشخيص والمنتج المسجل وملصق الاستخدام.")
 }
}

@Composable private fun CropsPage(){
 var selected by remember{mutableStateOf<Crop?>(null)}
 var search by remember{mutableStateOf("")}
 Column(Modifier.fillMaxSize().padding(16.dp)){
  Text("موسوعة المحاصيل",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  OutlinedTextField(search,{search=it},modifier=Modifier.fillMaxWidth(),label={Text("ابحث عن محصول")},singleLine=true)
  if(selected==null)LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(vertical=10.dp)){items(crops.filter{it.name.contains(search,true)}){c->Card(Modifier.fillMaxWidth().clickable{selected=c},colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Spa,null,tint=Forest);Spacer(Modifier.width(12.dp));Text(c.name,fontWeight=FontWeight.Bold,color=Ink);Spacer(Modifier.weight(1f));Icon(Icons.Default.ChevronLeft,null)}}}}
  else{TextButton(onClick={selected=null}){Text("رجوع إلى المحاصيل")};Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(9.dp)){Text(selected!!.name,fontSize=23.sp,fontWeight=FontWeight.Bold,color=Forest);InfoBlock("موعد الزراعة",selected!!.season);InfoBlock("التربة",selected!!.soil);InfoBlock("الري",selected!!.water);InfoBlock("العناية",selected!!.care);Text("المواعيد تقريبية وتتغير حسب الولاية والصنف والمناخ وتحليل التربة.",fontSize=12.sp,color=Color.Gray)}}
 }
}
@Composable private fun InfoBlock(title:String,body:String){Card(colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(13.dp)){Text(title,fontWeight=FontWeight.Bold,color=Forest);Text(body,color=Ink,lineHeight=22.sp)}}}

@Composable private fun SoilPage(){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("التربة والأراضي",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest);InfoBlock("التربة الرملية","تصريف سريع واحتفاظ أقل بالماء والعناصر غالباً؛ تساعد المادة العضوية والري المدروس.");InfoBlock("التربة الطينية","احتفاظ أعلى بالماء والعناصر وقد تتعرض للتغريق والانضغاط؛ حسّن الصرف وتجنب العمل وهي شديدة البلل.");InfoBlock("التربة الطميية","متوازنة نسبياً في الصرف والاحتفاظ بالماء، لكنها تحتاج تحليلاً لتحديد الخصوبة.");InfoBlock("تحاليل مهمة","درجة الحموضة pH، الملوحة EC، المادة العضوية، والآزوت والفوسفور والبوتاسيوم والعناصر الصغرى.");InfoBlock("قاعدة أساسية","لا تحدد صلاحية الأرض أو جرعة السماد من اللون أو الملمس وحدهما؛ اعتمد عينة ممثلة وتحليلاً مخبرياً.")}}

@Composable private fun FertilizerPage(){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("الأسمدة والمبيدات",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest);InfoBlock("الأسمدة العضوية","السماد العضوي المتحلل يحسن بنية التربة ومادتها العضوية. تجنب الروث الطازج قرب المحاصيل الحساسة.");InfoBlock("الأسمدة المعدنية","اختر التركيبة وفق تحليل التربة واحتياجات المحصول ومرحلة النمو، ولا تخلط المنتجات دون التحقق من التوافق.");InfoBlock("المبيدات","استخدم منتجاً مسجلاً للمحصول والآفة في بلدك، والتزم بالجرعة وفترة ما قبل الحصاد وفترة إعادة الدخول على الملصق.");InfoBlock("معدات الوقاية","ارتدِ معدات الوقاية المحددة على الملصق، وتجنب الرش مع الرياح أو قرب المياه والأطفال والحيوانات.");Text("لا يقدم التطبيق جرعات مبيدات أو خلطات علاجية عشوائية. ارجع إلى الملصق الرسمي والمهندس الزراعي المحلي.",color=Color(0xFF9B3F20),fontWeight=FontWeight.SemiBold)}}

@Composable private fun IrrigationPage(){
 var area by remember{mutableStateOf("1")};var depth by remember{mutableStateOf("10")};var result by remember{mutableStateOf("")}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("حاسبة مياه الري",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  Text("الحجم النظري بالمتر المكعب = المساحة بالهكتار × عمق الري بالملليمتر × 10.")
  OutlinedTextField(area,{area=it},label={Text("المساحة (هكتار)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
  OutlinedTextField(depth,{depth=it},label={Text("عمق الري (ملم)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
  Button(onClick={val a=area.replace(',','.').toDoubleOrNull();val d=depth.replace(',','.').toDoubleOrNull();result=if(a!=null&&d!=null&&a>0&&d>0)"الحجم النظري = %.1f متر مكعب".format(a*d*10)else"أدخل أرقاماً صحيحة أكبر من الصفر."},modifier=Modifier.fillMaxWidth()){Text("احسب كمية المياه")}
  if(result.isNotBlank())Text(result,fontWeight=FontWeight.Bold,color=Forest,fontSize=18.sp)
  InfoBlock("ملاحظة","الحساب لا يخصم المطر الفعال أو كفاءة شبكة الري ولا يحدد موعد الري. اضبط العمق وفق رطوبة التربة والمحصول والطقس.")
 }
}

@Composable private fun WeatherPage(){
 var lat by remember{mutableStateOf("35.55")};var lon by remember{mutableStateOf("6.17")};var status by remember{mutableStateOf("أدخل إحداثيات الحقل للحصول على الطقس الحالي.")};var loading by remember{mutableStateOf(false)}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("الطقس الزراعي",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  Text("الإحداثيات الابتدائية تقريبية لمدينة باتنة وليست تحديداً لموقعك.")
  OutlinedTextField(lat,{lat=it},label={Text("خط العرض")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
  OutlinedTextField(lon,{lon=it},label={Text("خط الطول")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
  Button(enabled=!loading,onClick={val la=lat.replace(',','.').toDoubleOrNull();val lo=lon.replace(',','.').toDoubleOrNull()
   if(la==null||lo==null||la !in -90.0..90.0||lo !in -180.0..180.0)status="الإحداثيات غير صحيحة."
   else{loading=true;status="جارٍ جلب الطقس...";Thread{try{val url=URL("https://api.open-meteo.com/v1/forecast?latitude=$la&longitude=$lo&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m");val c=url.openConnection() as HttpURLConnection;c.connectTimeout=10000;c.readTimeout=10000;val j=JSONObject(c.inputStream.bufferedReader().use{it.readText()}).getJSONObject("current");val msg="الحرارة: ${j.getDouble("temperature_2m")}°م\nالرطوبة: ${j.getInt("relative_humidity_2m")}%\nالهطول: ${j.getDouble("precipitation")} ملم\nسرعة الرياح: ${j.getDouble("wind_speed_10m")} كم/س";android.os.Handler(android.os.Looper.getMainLooper()).post{status=msg;loading=false};c.disconnect()}catch(_:Exception){android.os.Handler(android.os.Looper.getMainLooper()).post{status="تعذر جلب الطقس. تحقق من الإنترنت والإحداثيات ثم أعد المحاولة.";loading=false}}}.start()}
  },modifier=Modifier.fillMaxWidth()){Text(if(loading)"جارٍ التحميل..." else "جلب الطقس الحالي")}
  Card(colors=CardDefaults.cardColors(containerColor=Color.White)){Text(status,Modifier.padding(16.dp),color=Ink,lineHeight=24.sp)}
  Text("المصدر Open-Meteo. القراءة الحالية ليست توقعاً زراعياً متخصصاً أو تنبيهاً رسمياً.",fontSize=12.sp,color=Color.Gray)
 }
}

@Composable private fun SettingsPage(welcome:Boolean,onWelcome:(Boolean)->Unit){
 var language by remember{mutableStateOf("العربية")}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("الإعدادات واللغات والصوت",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){Text("الترحيب الصوتي",Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Switch(welcome,onWelcome)}
  Text("نص الترحيب: «مرحباً بك، أنا نورالدين، أهلاً وسهلاً بك في تطبيق نورالدين للزراعة»")
  Text("لغة الواجهة",fontWeight=FontWeight.Bold)
  listOf("العربية","الفرنسية","الإنجليزية").forEach{lang->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=language==lang,onClick={language=lang});Text(lang)}}
  Text("اختيار اللغة ظاهر هنا، والترجمة الكاملة لكل الشاشات تحتاج مرحلة توطين إضافية.",fontSize=12.sp,color=Color.Gray)
  InfoBlock("تحكم المالك","لوحة تحكم آمنة لإدارة الخطط والإعدادات والمحتوى تحتاج حساب مالك وخادماً خلفياً؛ لا تُحفظ صلاحيات الإدارة الحساسة داخل التطبيق وحده.")
 }
}

@Composable private fun ProfilePage(){
 var uri by remember{mutableStateOf<Uri?>(null)}
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri=it}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp),horizontalAlignment=Alignment.CenterHorizontally){
  Text("ملفي الشخصي وصورة الخلفية",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest)
  uri?.let{u->AndroidView(factory={ImageView(it).apply{scaleType=ImageView.ScaleType.CENTER_CROP;setImageURI(u)}},modifier=Modifier.size(190.dp))}?:Box(Modifier.size(150.dp).background(Color(0xFFE4EEDF),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Default.Person,null,tint=Forest,modifier=Modifier.size(70.dp))}
  Button(onClick={picker.launch("image/*")}){Icon(Icons.Default.AddPhotoAlternate,null);Text("اختيار صورتي")}
  Text("هذه معاينة للصورة في الجلسة؛ الحفظ الدائم ومزامنتها مع صورة المساعد والخلفية يحتاجان تخزيناً آمناً.")
  InfoBlock("الهوية البصرية","نحافظ على هوية Nouradine Agriculture الخضراء والذهبية والمرجع الذي اخترته، ولا نستبدله دون موافقتك.")
 }
}

@Composable private fun SubscriptionPage(){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("الاشتراك والتحديثات",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest);InfoBlock("التجربة المجانية","30 يوماً من بداية الاشتراك.");InfoBlock("الاشتراك","8 دولارات لكل دورة تجديد مدتها شهران وفق المواصفات. لم يتم تفعيل الدفع أو الخصم بعد.");InfoBlock("التحديثات","التحديث الدوري كل شهرين، مع إصلاحات أمنية وضرورية عند الحاجة.");InfoBlock("العملة الخاصة","مؤجلة إلى ما بعد انتشار التطبيق ونجاحه؛ لا توجد عملة داخلية حالياً.");Text("يلزم ربط بوابة دفع تدعم بلد صاحب التطبيق قبل تفعيل الشراء.",fontSize=12.sp,color=Color.Gray)}}

@Composable private fun CallerInfoPage(){Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("دليل الاتصالات وتعريف المتصل",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest);InfoBlock("حالة الميزة","لم يتم تفعيل تعريف المتصل أو البحث عن أرقام الهواتف.");InfoBlock("المتطلبات","تتطلب الخدمة مزود بيانات قانونياً وموافقة المستخدم وصلاحيات Android المناسبة وسياسة خصوصية واضحة.");Text("لن نعرض هوية رقم هاتف بالتخمين أو نجمع جهات الاتصال دون إذن صريح.",color=Color(0xFF9B3F20))}}

@Composable private fun ToolsPage(go:(String)->Unit){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("الأدوات الزراعية",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Forest);listOf("موسوعة المحاصيل","التربة والأراضي","الأسمدة والمبيدات","الري والتسميد","الطقس والتنبيهات").forEach{name->OutlinedButton(onClick={go(name)},modifier=Modifier.fillMaxWidth()){Text(name);Spacer(Modifier.weight(1f));Icon(Icons.Default.ChevronLeft,null)}}}}
