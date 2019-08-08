package com.muiezarif.muiez.comforthajj;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GuideTopic extends AppCompatActivity {
    TextView topic,explanation;
    Intent intent;
    String topicname;
    DatabaseReference presenceReference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_topic);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        intent=getIntent();
        topicname=intent.getStringExtra("topic").toString();
        topic=findViewById(R.id.tvTopic);
        explanation=findViewById(R.id.tvExplanation);
        if (topicname.equals("State of Ihram")){
            topic.setText("State of Ihram");
            explanation.setText("This is considered the first step for any pilgrim wishing to perform hajj. To enter the state of Ihram, a pilgrim has to recite an intention to perform hajj called the Talabiya. This is when a pilgrim prepares one’s soul, mind and body for journey to the Almighty God. Entering the stage begins from the Miqat, or a place that is outside the pilgrimage area.\n" +
                    "\n" +
                    "Men and women going on hajj adhere to a specific dress code which is aimed at showing modesty and shedding all signs of wealth. Men don unstitched white garments, while women wear normal stitched clothes and a headscarf. Women are forbidden however from wearing the burqa or niqab.\n" +
                    "\n" +
                    "In fact, the word Ihram originates from the Arabic term Tahreem, which means prohibited. Because the state is believed to have a special essence of spiritual purity, there are certain acts that are not allowed for pilgrims. Among them are using perfumes, cutting hair or nails, and slaughtering animals.");
        }else if (topicname.equals("Mecca")){
            topic.setText("Mecca");
            explanation.setText("The Saudi Arabian city is considered Islam’s holiest site, as it holds al-Masjid al-Haram or the Grand Mosque that surrounds the Kaaba, a cuboid shaped building which Muslims believe has been put up together by Prophet Ibrahim and his son Ismail almost 4, 000 years ago.\n"+"\n" +
                    "Muslims call the Kaaba “the house of God” and are expected to face the direction of Mecca when praying in any part of the world.");
        }else if(topicname.equals("Tawaf")){
            topic.setText("Tawaf");
            explanation.setText("Upon arrival to Mecca, Pilgrims should make Tawaf or circumambulation. It is considered an integral part of the pilgrimage, and refers to the seven times pilgrims circle around the Kaaba at the beginning, during and at the end of hajj.\n" +
                    "\n" +
                    "The circuits are done in a counter-clockwise direction and are thought to express the unity between Muslims in worshipping one God. The rotations are marked by al-Hajar al-Aswad, or the Black Stone at the eastern corner of the Kabaa.");
        }else if(topicname.equals("Sa'ey")){
            topic.setText("Sa'ey");
            explanation.setText("To traverse the distance between the hills of Safa and Marwah for seven times, this is what is called Sa’ey. The term in Arabic means to walk or move quickly.\n" +
                    "\n" +
                    "After Tawaf, pilgrims perform Sa’ey, in what commemorates the journey by Prophet Ibrahim’s wife to find water for her infant prophet Ismail, after they were left in the desert of Mecca at God’s command. The hills are now enclosed by the Grand Mosque.");
        }else if(topicname.equals("Departure to mina")){
            topic.setText("Departure to mina");
            explanation.setText("Pilgrims proceed to the tent city of Mina on the first day of hajj or what is called the day of Tarwiah. They converge to Mina for prayer, which lies roughly eight kilometers away from Mecca. Pilgrims are required to remain in Mina until the sunrise of the second day of hajj, where they leave to Arafat.\n" +
                    "\n" +
                    "They pay another trip to Mina on the third day of hajj to perform the symbolic stoning of the devil, the sixth rite of hajj.");
        }else if(topicname.equals("Mount Arafat")){
            topic.setText("Mount Arafat");
            explanation.setText("After the dawn prayers in Mina, pilgrims start their journey to the desert planes of Arafat. Dubbed as the “most important day of hajj,” Muslims spend the day of Arafat in the vicinity of the mountain, praying and repenting.\n"+"\n"+
                    "The rituals of this day end at sunset, when pilgrims move to Muzdalifah.");
        }else if(topicname.equals("Muzdalifah")){
            topic.setText("Muzdalifah");
            explanation.setText("After descending from Arafat, pilgrims arrive to the open land of Muzdalifah, southeast of Mina. People gather in makeshift tents and are required to perform Maghrib and Isha prayers. It is also considered the best place to collect pebbles for Ramy al-Jamarat.");
        }else if(topicname.equals("Ramy al-Jamarat")){
            topic.setText("Ramy al-Jamarat");
            explanation.setText("The symbolic stoning of the devil, where pilgrims fling pebbles, called jamarat, at three walls, in the city of Mina. The stoning marks the third day of hajj or Eid al-Adha.");
        }else if(topicname.equals("Eid al-Adha")){
            topic.setText("Eid al-Adha");
            explanation.setText("The Eid al-Adha festival, or the Feast of Sacrifice, is celebrated by Muslims who are not on pilgrimage by slaughtering animals to mark Prophet Ibrahim’s willingness to sacrifice his son Ismail upon the command of God.\n" +
                    "\n" +
                    "Pilgrims spend the three days of Eid stoning pillars that represent the devil.\n" +
                    "\n" +
                    "They later purchase tokens to have a sheep slaughtered in the Mecca neighbourhood of Mina.");
        }else if (topicname.equals("احرم کی حالت")){
            topic.setText("احرم کی حالت");
            explanation.setText("حج حج انجام دینے کے خواہاں کسی حجاج کا یہ پہلا قدم ہے. حرم کی حالت میں داخل ہونے کے لئے، حجاب طلبی کو نامزد کرنے کے لئے ایک حجاج کا ارادہ رکھتا ہے. یہ ہے جب ایک حجاج خدا کی طرف سفر کرنے کے لئے کسی کی روح، دماغ اور جسم کو تیار کرتا ہے. مرحلے میں داخلہ شروع ہونے والے میقات سے شروع ہوتا ہے، یا ایسی جگہ جو حج کے علاقے سے باہر ہے.\n" +
                    "\n" +
                    "حج پر جانے والے مردوں اور عورتوں کو مخصوص لباس کوڈ کا تعاقب کرنا ہے جو مقصد کے تمام علامات کو عدم برداشت اور شیڈنگ کا سامنا کرنا ہے. مرد غیر معمولی سفید لباس پہنتے ہیں، جبکہ خواتین عام سلیما کپڑے اور سر کے سر پہنے جاتے ہیں. برقعہ یا نقیاب پہننے کے باوجود خواتین حرام ہیں.\n" +
                    "\n" +
                    "اصل میں، لفظ کا لفظ عربی اصطلاح طاہر سے پیدا ہوتا ہے جس کا مطلب ممنوع ہے. کیونکہ ریاست کا خیال ہے کہ روحانی پاکیزگی کا ایک خاص مقصد ہے، وہاں کچھ ایسے اعمال ہیں جنہیں حاجیوں کی اجازت نہیں ہے. ان میں سے ایک دوسرے کو خوشبو، بالوں یا ناخن کاٹنے اور جانوروں کو مارنے کا استعمال کر رہے ہیں.");
        }else if (topicname.equals("مکہ")){
            topic.setText("مکہ");
            explanation.setText("سعودی عرب کے شہر اسلام کی سب سے ساری سائٹ پر غور کیا جاتا ہے، کیونکہ یہ مسجد الاسلام یا حدیث جس میں کعبہ کی گردش ہوتی ہے، کیوبائڈ سائز کی عمارت ہے جس میں مسلمان ایمان لائے ہیں کہ ابراہیم ابراہیم اور اس کے بیٹے اسماعیل تقریبا 4، 000 سال پہلے.\n"+"\n"+
                    "مسلمان کعبہ کو \"خدا کے گھر\" کہتے ہیں اور امید کرتے ہیں کہ مکہ کی سماعت دنیا کے کسی بھی حصے میں کرتے ہیں.\n");
        }else if(topicname.equals("توفف")){
            topic.setText("توفف");
            explanation.setText("مکہ تک پہنچنے پر، حجابوں کو طواف یا سنبھالنے کا موقع دینا چاہئے. یہ حج کی ایک لازمی حصہ سمجھا جاتا ہے، اور حجر کے اختتام پر، اوقات میں اور کعبہ کے سات اوقات حاجیوں کو دائرے میں دھیان دیتا ہے.\n" +
                    "\n" +
                    "سرکٹس ایک گھڑی کی طرف سے سمت میں کیا جاتا ہے اور خیال کیا جاتا ہے کہ ایک خدا کی عبادت میں مسلمانوں کے درمیان اتحاد کا اظہار کیا جائے. گردش الظواہری الاسد، یا کبہ کے مشرقی کنارے پر سیاہ پتھر کی طرف سے نشان لگا دیا گیا ہے.");
        }else if(topicname.equals("سعی")){
            topic.setText("سعی");
            explanation.setText("صفا اور مروہ کے پہاڑوں کے درمیان فاصلے کو سات دفعہ دور کرنے کے لئے، یہ سائے کہا جاتا ہے. عربی میں اصطلاح کا مطلب چلتا ہے یا جلدی چلتا ہے.\n" +
                    "\n" +
                    "طواف کے بعد، حاجیوں نے سعی کو انجام دیا، اس کے بعد نبی اکرم صلی اللہ علیہ وسلم کی بیوی نے اپنے بچے نبی اکرم صلی اللہ علیہ وآلہ وسلم کو پانی کے حکم پر مکہ کے صحرا میں چھوڑنے کے بعد پانی کی تلاش کا ذکر کیا. پہاڑی مسجد اب گرینڈ مسجد سے منسلک ہیں.");
        }else if(topicname.equals("مینا کی روانگی")){
            topic.setText("مینا کی روانگی");
            explanation.setText("حج حج کے پہلے دن مینا کے خیمے شہر پر چلتے ہیں یا طریہ کا دن کہتے ہیں. وہ مکہ سے تقریبا 8 کلومیٹر دور واقع نماز پڑھنے کے لئے مینا سے متفق ہیں. حج کے دوسرے روز سورج دورے تک حیدرآباد میں مینا میں رہنا ضروری ہے، جہاں وہ عرفات سے نکل جاتے ہیں.\n" +
                    "\n" +
                    "وہ حج کے تیسرے دن مینا کے لئے ایک اور سفر ادا کرتے ہیں تاکہ شیطان کی علامتی عہد کا مظاہرہ کریں، حج کے چھٹے حصہ.");
        }else if(topicname.equals("عرفات پہاڑ")){
            topic.setText("عرفات پہاڑ");
            explanation.setText("مینا میں صبح کی صبح کے بعد، حاجیوں نے عرفات کے صحرا طیاروں کے سفر کا آغاز کیا. \"حج کے سب سے اہم دن\" کے طور پر ڈوب گئے، \"مسلمانوں نے پہاڑ کے ارد گرد، عرفات اور توبہ کرنے میں عرفات کا دن خرچ کیا.\n"+"\n"+
                    "اس دن کے مراحل غروب آفتاب پر، جب حاجیوں نے مظفرفاہ کو منتقل کیا.");
        }else if(topicname.equals("مظفرفا")){
            topic.setText("مظفرفا");
            explanation.setText("عرفات سے اترنے کے بعد، حاجیوں نے مینا کے جنوب مشرقی، مظفرالہ کی کھلی زمین پر پہنچائی. لوگ سازی خیموں میں جمع ہوتے ہیں اور مغرب اور اسحاق کی نماز انجام دینے کی ضرورت ہے. یہ رامی الجماری کے لئے کناروں کو جمع کرنے کا بہترین مقام بھی سمجھا جاتا ہے.");
        }else if(topicname.equals("رامی الجمیر")){
            topic.setText("رامی الجمیر");
            explanation.setText("\n" +
                    "شیطان کا علامتی استقبال، جہاں حاجیوں نے کناروں کو پھینک دیا، جن میں تین دیواروں پر مارت شہر کے شہر جمامر کہا جاتا تھا. ہجوم یا عید الاہدا کے تیسرے دن کا استنبول.");
        }else if(topicname.equals("عید الاہھا")){
            topic.setText("عید الاہھا");
            explanation.setText("عید الہہا تہوار، یا قربانی کی دعوت، مسلمانوں کی طرف سے منایا جاتا ہے جو جانوروں کو مار کر خدا کے حکم پر اپنے بیٹے اسماعیل کو قربانی کرنے کے لئے نبی ابراہیم کی قربانی کی نشاندہی کرنے کے لئے حیات نہیں ہیں.\n" +
                    "\n" +
                    "قربانی شیطان کی نمائندگی کرتے ہیں کہ تین دن کے عید اسٹوننگ ستونوں کو خرچ کرتے ہیں.\n" +
                    "\n" +
                    "بعد میں وہ مینا کے مکہ کے پڑوسی میں مارے ہوئے بھیڑوں کو ٹوکین خریدتے ہیں.");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
