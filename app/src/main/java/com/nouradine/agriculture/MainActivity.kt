package com.nouradine.agriculture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Forest = Color(0xFF245B35)
private val Leaf = Color(0xFF4E8B45)
private val Gold = Color(0xFFC69A42)
private val Cream = Color(0xFFF5F7EF)
private val Ink = Color(0xFF203126)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NouradineApp() }
    }
}

private data class Feature(val title: String, val subtitle: String, val icon: ImageVector, val detail: String)

private val features = listOf(
    Feature("المساعد الزراعي الذكي", "اسأل عن الزراعة واحصل على إرشادات", Icons.Default.Psychology, "واجهة المساعد جاهزة. ربط الذكاء الاصطناعي يحتاج إلى خدمة API آمنة وخادم خلفي قبل تفعيل الإجابات الحقيقية."),
    Feature("تشخيص أمراض النباتات", "تحليل صورة النبات", Icons.Default.CenterFocusStrong, "ستضاف هنا إمكانية اختيار صورة للنبات وإرسالها إلى نموذج تشخيص موثوق. التشخيص الآلي غير مفعّل في هذه النسخة."),
    Feature("موسوعة المحاصيل", "معلومات الزراعة والعناية", Icons.Default.MenuBook, "مكتبة المحاصيل ستضم مواعيد الزراعة والحصاد واحتياجات الماء والتربة لكل محصول."),
    Feature("التربة والأراضي", "خصائص التربة وتحسينها", Icons.Default.Landscape, "إرشادات أنواع التربة وقياس الحموضة والملوحة وتحسين خصوبة الأرض."),
    Feature("الأسمدة والمبيدات", "الاستخدام الآمن والصحيح", Icons.Default.Science, "معلومات الاستخدام والجرعات والتحذيرات ستُبنى على مراجع زراعية موثوقة وملصقات المنتجات المحلية."),
    Feature("الري والتسميد", "تنظيم احتياجات المحاصيل", Icons.Default.WaterDrop, "أدوات لحساب الاحتياجات المائية وبرامج التسميد حسب المحصول والمرحلة ونوع التربة."),
    Feature("الطقس والتنبيهات", "تابع حالة الطقس الزراعي", Icons.Default.Cloud, "ربط الطقس والتنبيهات المحلية يحتاج إلى مزود بيانات وموقع يختاره المستخدم."),
    Feature("الإعدادات واللغات", "تحكم في تجربة التطبيق", Icons.Default.Settings, "إعدادات اللغة والصوت والمظهر والحساب والاشتراك ستضاف تدريجياً."),
    Feature("ملفي الشخصي", "تخصيص الصورة والخلفية", Icons.Default.Person, "يمكن إضافة صورة شخصية وخلفية يختارها صاحب الحساب بعد تنفيذ التخزين والصلاحيات."),
    Feature("التحديثات والاشتراك", "إدارة النسخة والخطة", Icons.Default.Upgrade, "لن يتم تفعيل الدفع قبل تحديد بوابة دفع متاحة قانونياً وعملياً في بلد المستخدم، مع تجربة مجانية مدتها 30 يوماً ودورة اشتراك كل شهرين وفق الاتفاق."),
    Feature("دليل الاتصالات", "ميزة تعريف المتصل", Icons.Default.Call, "أي ميزة لتعريف المتصل تتطلب مزود بيانات وصلاحيات ومراجعة الخصوصية والقوانين؛ ليست مفعّلة حالياً.")
)

@Composable
private fun NouradineApp() {
    var selected by remember { mutableStateOf<Feature?>(null) }
    var query by remember { mutableStateOf("") }
    MaterialTheme(colorScheme = lightColorScheme(primary = Forest, secondary = Gold, background = Cream, surface = Color.White)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
            if (selected == null) {
                HomeScreen(query = query, onQuery = { query = it }, onSelect = { selected = it })
            } else {
                DetailScreen(feature = selected!!, onBack = { selected = null })
            }
        }
    }
}

@Composable
private fun HomeScreen(query: String, onQuery: (String) -> Unit, onSelect: (Feature) -> Unit) {
    val filtered = features.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("NOURADINE", color = Forest, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("نورالدين للزراعة", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                    Text("رفيقك نحو زراعة أفضل", color = Leaf, fontSize = 14.sp)
                }
                Box(Modifier.size(54.dp).background(Forest, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Forest), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("مرحباً بك في عالم الزراعة الذكية", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text("اكتشف الأدوات الزراعية والإرشادات في مكان واحد.", color = Color(0xFFE5F0E2), fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = Gold)
                        Spacer(Modifier.width(6.dp))
                        Text("معاً من أجل محصول أفضل", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        item {
            OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(), placeholder = { Text("ابحث عن خدمة زراعية...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        }
        item { Text("الخدمات الزراعية", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        items(filtered) { feature -> FeatureCard(feature = feature, onClick = { onSelect(feature) }) }
        item {
            Text("نسخة تأسيسية قيد التطوير — الخدمات المتصلة والذكاء الاصطناعي والدفع لم تُفعّل بعد.", color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color(0xFFEAF2E7), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(feature.icon, contentDescription = null, tint = Forest, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(feature.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(feature.subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Leaf)
        }
    }
}

@Composable
private fun DetailScreen(feature: Feature, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = Forest) }
            Text("العودة إلى الرئيسية", color = Forest, fontWeight = FontWeight.SemiBold)
        }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Forest)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(feature.icon, contentDescription = null, tint = Gold, modifier = Modifier.size(46.dp))
                Text(feature.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(feature.subtitle, color = Color(0xFFE5F0E2), textAlign = TextAlign.Center)
            }
        }
        Text("عن هذه الخدمة", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(feature.detail, color = Ink, fontSize = 15.sp, lineHeight = 25.sp)
        Spacer(Modifier.weight(1f))
        Text("سيتم تفعيل هذه الخدمة بعد ربطها بمصدر بيانات موثوق واختبارها.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}
