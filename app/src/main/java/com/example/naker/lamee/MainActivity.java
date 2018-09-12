package com.example.naker.lamee;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tomer.fadingtextview.FadingTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FadingTextView fadingTextView;
    TextView submit, question, head, stop;
    AutoCompleteTextView selfQ;
    AutoCompleteTextView answer;
    RadioButton yes;
    RadioButton no;
    RadioGroup radioGroup;

    Animation fadeIn,fadeOut;
    AnimationSet animation;

    ConstraintLayout layout;

    AnimationManager Amanager;
    DBManager database;

    int questionCount;
    int basicCount=5;
    int finalCount=0;
    int showCount=0;

    boolean afterM = false;
    boolean insert = false;
    boolean finalFlag = true;

    String username;
    int age;
    Map<String,String> userInfo;
    List<Map<String,String>> relations;

    List<Map<String,String>> duplications;
    ListView listView;

    AutoCompleteTextView dropAnswer;

    private static final String[] RELATIONS = {
            "어머니",
            "아버지",
            "형제 자매 남매",
            "할아버지",
            "할머니",
            "친구",
            "선생님",
            "배우자",
            "자식",
    };

    String[] questions = new String[] {
            "당신이 '힘들때 도와준 사람'은\n누구입니까?",
            "당신이 '슬플때 같이 슬퍼해준 사람'은\n누구입니까?",
            "당신의 '외로움을 잊게 만들어준 사람'은\n누구입니까?",
            "당신을 '바른길로 이끌어준 사람'은\n누구입니까?",
            "지금 '바로 떠오르는 사람'이\n누구입니까?",
            "지금의 '당신을 존재하게 만든 사람'은\n누구입니까?",
            "finish"
    };

    ArrayAdapter<String> adapter;

    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        service = new Intent(this,BgmService.class);
        startService(service);

        database = DBManager.getManager(this);
        database.open();

        layout = findViewById(R.id.layout);

        submit = findViewById(R.id.submit);
        question = findViewById(R.id.textView);
        answer = findViewById(R.id.answer);
        selfQ = findViewById(R.id.selfQ);

        head = findViewById(R.id.head);
        stop = findViewById(R.id.complete);

        dropAnswer = findViewById(R.id.dropAnswer);

        radioGroup = findViewById(R.id.radioGroup);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        yes.setChecked(false);
        no.setChecked(false);

        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,RELATIONS);

        dropAnswer.setAdapter(adapter);
        dropAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    dropAnswer.showDropDown();
                }
            }
        });

        submit.setVisibility(View.GONE);
        Amanager = AnimationManager.getAnimationManager();

        SystemClock.sleep(500);

        fadingTextView = findViewById(R.id.tomer);
        fadingTextView.setTimeout(FadingTextView.SECONDS,2);

        fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        fadeOut = new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        animation = new AnimationSet(false);

        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(8000);
                afterM = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fadingTextView.pause();
                        SystemClock.sleep(500);

                        fadingTextView.setTexts(new String[] {"시작하려면 눌러주세요", "Press to Start"});
                        fadingTextView.setTextSize(25);
                        fadingTextView.resume();
                    }
                });
            }
        }.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void start(View view) {
        if(afterM){
            fadingTextView.pause();
            Amanager.crossFade(fadingTextView,false);
            Amanager.crossFade(submit,true);

            TransitionManager.beginDelayedTransition(layout);

            makeInput("당신의 이름은 무엇인가요?",600,true, "",0);

            afterM = false;
            questionCount = 0;
        }
    }

    public void next(View view){

        String s_answer = answer.getText().toString();

        Amanager.crossFade(question,false);
        Amanager.crossFade(answer,false);
        Amanager.crossFade(radioGroup,false);
        Amanager.crossFade(dropAnswer,false);





        int length = questions.length;
        int finishPoint = length+basicCount;

        if(questionCount < finishPoint) {
            switch(questionCount){
                case 0:
                    username = s_answer;
                    makeInput("나이가 어떻게 되시나요?", 600,true, "숫자",0);
                    break;
                case 1:
                    age = Integer.parseInt(s_answer);
                    int auth = auth(username,age);
                    if(auth == 0){
                        makeInput("좋아요\n준비되면 아래의 버튼을 눌러주세요", 800,false, "",2);
                    } else{
                        if(!yes.isChecked() && !no.isChecked()){
                            makeInput("데이터베이스에 "+auth+"명 존재합니다\n본인이 있습니까?", 700,true, "yn",2);
                            questionCount--;
                        } else if(yes.isChecked() && !no.isChecked()){
                            duplication();
                            radioGroup.clearCheck();
                            return;
                        } else if(no.isChecked() && !yes.isChecked()){
                            makeInput("그럼 "+username+(auth+1)+"(으)로\n저장하겠습니다", 700,false, "",2);
                            username = username+(auth+1);
                            radioGroup.clearCheck();
                        }
                    }
                    break;
                case 2:
                    if(!database.insertUser(username,age)){
                        Toast.makeText(this,"데이터 저장 실패",Toast.LENGTH_SHORT).show();
                    }
                    makeInput("시작하기 앞서\n앞으로 나오는 질문에\n깊게 생각하기를 추천합니다.",700,false,"",2);
                    break;
                case 3:
                    userInfo = database.getData("name = '"+username+"' and age = "+age);
                    makeInput("시작합니다!", 800,false, "",2);
                    break;
                case 4:
                    makeInput("다음은 필수질문들입니다",800,false,"",2);
                    break;
                default:
                    if(insert){
                        String q = question.getText().toString();
                        q = q.split("\'")[1];
                        String n = s_answer;
                        String r = dropAnswer.getText().toString();

                        int user_id = Integer.parseInt(userInfo.get("_id"));

                        if(!database.insertRelation(q,n,r,user_id)){
                            Toast.makeText(this,q+" "+n+" "+r,Toast.LENGTH_SHORT).show();
                        }
                        insert = false;
                    }
                    if(questions[questionCount-basicCount].equals("finish")){
                        makeInput("끝이 났습니다!", 800,false, "",2);
                        submit.setOnClickListener(finishListener);
                    } else{
                        makeInput(questions[questionCount-basicCount], 600,true, "drop",0);
                    }
                    break;
            }
        }

        questionCount++;
    }

    public void duplication(){
        duplications = database.duplicationList(username);

        List<Map<String,String>> list = new ArrayList<>();

        for(int i = 0; i < duplications.size(); i++){
            Map<String,String> map = new HashMap<>();
            StringBuilder sb = new StringBuilder();

            map.put("fuck",duplications.get(i).get("name"));

            for(int j=0; j<duplications.get(i).get("relations").split(",").length; j++){
                sb.append(duplications.get(i).get("relations").split(",")[j]).append(" ").append(duplications.get(i).get("people").split(",")[j]).append("\n");
            }


            map.put("people", sb.toString());
            map.put("_id",duplications.get(i).get("_id"));

            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.duplication,
                new String[] {"name","people"},
                new int[] {R.id.name,R.id.relations}
        );
        head.setText("엄마를 골라주세요");

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,String> map = (Map<String,String>)adapterView.getItemAtPosition(i);

                userInfo = database.getData(map.get("_id"));
                Amanager.crossFade(listView,false);
                Amanager.crossFade(submit,true);
                Amanager.crossFade(head,false);

                submit.setOnClickListener(finishListener);
                finalCount = 3;
                submit.performClick();
                return;
            }
        });

        Amanager.crossFade(head,true);
        Amanager.crossFade(submit,false);
        Amanager.crossFade(listView,true);
    }

    View.OnClickListener finishListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Amanager.crossFade(question,false);
            Amanager.crossFade(answer,false);
            Amanager.crossFade(radioGroup,false);
            Amanager.crossFade(dropAnswer,false);
            Amanager.crossFade(selfQ,false);

            if(finalFlag){
                switch (finalCount){
                    case 0:
                        makeInput("지금부터는 직접!\n부모를 만들어서",800,false,"",2);
                        break;
                    case 1:
                        makeInput("본인의 데이터베이스에",800,false,"",2);
                        break;
                    case 2:
                        makeInput("부모를 추가해주세요",800,false,"",2);
                        break;
                    default:
                        if(finalCount >= 4){
                            String q = selfQ.getText().toString();
                            String n = answer.getText().toString();
                            String r = dropAnswer.getText().toString();

                            int user_id = Integer.parseInt(userInfo.get("_id"));

                            if(!database.insertRelation(q,n,r,user_id)){
                                Toast.makeText(getApplicationContext(),q+" "+n+" "+r,Toast.LENGTH_SHORT).show();
                            }
                        }
                        makeInput("editText",600,true,"drop",0);
                        break;
                }
                finalCount++;
            } else{
                Amanager.crossFade(head,false);
                Amanager.crossFade(stop,false);
                if(yes.isChecked()){
                    relations = database.getRelations(Integer.parseInt(userInfo.get("_id")));
                    submit.setOnClickListener(finalListing);
                    submit.performClick();
                    radioGroup.clearCheck();
                    return;
                } else if(no.isChecked()){
                    finalFlag = true;

                    radioGroup.clearCheck();

                    submit.performClick();
                    return;
                }

                makeInput("바로 이전에 작성한 정보는\n저장되지 않습니다\n그만하시겠습니까?",700,true,"yn",2);

            }

        }
    };


    View.OnClickListener finalListing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Amanager.crossFade(question,false);
            Amanager.crossFade(answer,false);
            Amanager.crossFade(radioGroup,false);
            Amanager.crossFade(dropAnswer,false);
            Amanager.crossFade(selfQ,false);

            int endingCount;

            String[] ending = new String[] {
                    "자금 적으신 분들에게",
                    "감사의 말씀을 전하셨나요?",
                    "그분들이 힘드실때",
                    username+"님께서도 힘이 되주셨나요?",
                    "그렇지 않다면",
                    "지금",
                    "감사의 인사 한마디",
                    "전달하는게 어떨까요?",
                    "저의 역할은\n여기서 끝입니다",
                    "허무하신가요?",
                    "그랬다면 죄송합니다 ㅜㅜ",
                    "하지만 저 \'라미\'를 계기로",
                    "주변 사람의 고마움을",
                    "다시 한번",
                    "생각할 시간을",
                    "가지셨으면 합니다",
                    "그럼",
                    "더욱 성장해서",
                    "돌아오겠습니다!",
                    "종료하려면 버튼을 누르세요"
            };

            int relationCount = relations.size() - 1;

            if(showCount > relationCount){

                endingCount = showCount - relationCount - 1;

                if(endingCount > ending.length - 1){
                    finish();
                    return;
                }
                makeInput(ending[endingCount],800,false,"",2);
                showCount++;
                return;
            }

            Map<String,String> relation = relations.get(showCount);

            String expression = relation.get("question")+"\n"+relation.get("relation")+" "+relation.get("name");
            makeInput(expression,700,false,"",2);

            showCount++;
        }
    };

    public void stop(View view) {

        finalFlag = false;
        submit.performClick();
    }

    public int auth(String username, int age){
        String sql = "SELECT * FROM "+DBManager.TABLE_NAME+" WHERE name LIKE'"+username+"%' and age = "+age;
        Cursor cursor = database.rawQuery(sql);



        return cursor.getCount();
    }

    public void makeInput(final String str, final int topMargin, final boolean hasAnswer, final String answerType, final int position){

        final ConstraintSet constraintSet = new ConstraintSet();
        final int basicMargin = 33;

        new Thread(){
            @Override
            public void run() {

                SystemClock.sleep(1000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View contMain;

                        if(str.equals("editText")){
                            contMain = (View) selfQ;

                            selfQ.setText("");
                            selfQ.setTextSize(20);
                            selfQ.setHint("그 사람의 수식어를 적어주세요");
                            selfQ.setGravity(Gravity.START);
                            selfQ.setShadowLayer(5,0,0, Color.WHITE);

                            selfQ.setVisibility(View.GONE);
                            Amanager.crossFade(selfQ,true);

                            constraintSet.constrainHeight(selfQ.getId(),ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainWidth(selfQ.getId(),ConstraintSet.MATCH_CONSTRAINT);

                            constraintSet.connect(selfQ.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,basicMargin);
                            constraintSet.connect(selfQ.getId(),ConstraintSet.RIGHT,layout.getId(),ConstraintSet.RIGHT,basicMargin);
                            constraintSet.connect(selfQ.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,topMargin);

                            question.setVisibility(View.GONE);
                            Amanager.crossFade(question,false);

                            constraintSet.constrainHeight(question.getId(),ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainWidth(question.getId(),ConstraintSet.MATCH_CONSTRAINT);

                            constraintSet.connect(question.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,basicMargin);
                            constraintSet.connect(question.getId(),ConstraintSet.RIGHT,layout.getId(),ConstraintSet.RIGHT,basicMargin);
                            constraintSet.connect(question.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,topMargin);

                            constraintSet.constrainHeight(head.getId(),ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainWidth(head.getId(),ConstraintSet.MATCH_CONSTRAINT);
                            constraintSet.connect(head.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,basicMargin);
                            constraintSet.connect(head.getId(),ConstraintSet.RIGHT,layout.getId(),ConstraintSet.RIGHT,basicMargin);
                            constraintSet.connect(head.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,130);

                            stop.setTextSize(15);

                            if(finalCount <= 4){
                                head.setText("직접 추가하기");
                                head.setVisibility(View.GONE);
                                Amanager.crossFade(head,true);

                                stop.setVisibility(View.GONE);
                                Amanager.crossFade(stop,true);
                            }

                            constraintSet.constrainHeight(stop.getId(),ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainWidth(stop.getId(),ConstraintSet.MATCH_CONSTRAINT);
                            constraintSet.connect(stop.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,basicMargin);
                            constraintSet.connect(stop.getId(),ConstraintSet.RIGHT,layout.getId(),ConstraintSet.RIGHT,basicMargin);
                            constraintSet.connect(stop.getId(),ConstraintSet.BOTTOM,layout.getId(),ConstraintSet.BOTTOM,500);


                        } else{
                            contMain = (View)question;

                            question.setText(str);
                            switch(position){
                                case 0:
                                    question.setGravity(Gravity.START);
                                    break;
                                case 1:
                                    question.setGravity(Gravity.END);
                                    break;
                                default :
                                    question.setGravity(Gravity.CENTER);
                                    break;
                            }
                            question.setTextSize(25);
                            question.setShadowLayer(5,0,0, Color.WHITE);

                            question.setVisibility(View.GONE);
                            Amanager.crossFade(question,true);

                            constraintSet.constrainHeight(question.getId(),ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainWidth(question.getId(),ConstraintSet.MATCH_CONSTRAINT);

                            constraintSet.connect(question.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,basicMargin);
                            constraintSet.connect(question.getId(),ConstraintSet.RIGHT,layout.getId(),ConstraintSet.RIGHT,basicMargin);
                            constraintSet.connect(question.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,topMargin);
                        }

                        if(hasAnswer) {
                            if(answerType.equals("yn")){

                                yes.setText("네");
                                yes.setTextSize(20);
                                yes.setVisibility(View.GONE);
                                Amanager.crossFade(yes,true);

                                no.setText("아니요");
                                no.setTextSize(20);
                                no.setVisibility(View.GONE);
                                Amanager.crossFade(no,true);

                                constraintSet.constrainHeight(radioGroup.getId(),ConstraintSet.WRAP_CONTENT);
                                constraintSet.constrainWidth(radioGroup.getId(),ConstraintSet.MATCH_CONSTRAINT);

                                constraintSet.connect(radioGroup.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, basicMargin);
                                constraintSet.connect(radioGroup.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, basicMargin);
                                constraintSet.connect(radioGroup.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 30);

                                Amanager.crossFade(radioGroup,true);
                            } else{
                                answer.setHint("여기에 입력하세요");
                                answer.setRawInputType(InputType.TYPE_CLASS_TEXT);
                                if(answerType.equals("숫자")){
                                    answer.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                                }

                                answer.setText("");

                                answer.setVisibility(View.GONE);

                                Amanager.crossFade(answer, true);

                                constraintSet.constrainHeight(answer.getId(), ConstraintSet.WRAP_CONTENT);
                                constraintSet.constrainWidth(answer.getId(), ConstraintSet.MATCH_CONSTRAINT);

                                if(answerType.equals("drop")){
                                    insert = true;

                                    dropAnswer.setText("");
                                    dropAnswer.setHint("관계");
                                    dropAnswer.setVisibility(View.GONE);
                                    Amanager.crossFade(dropAnswer,true);

                                    constraintSet.constrainHeight(dropAnswer.getId(), ConstraintSet.WRAP_CONTENT);
                                    constraintSet.constrainWidth(dropAnswer.getId(), ConstraintSet.MATCH_CONSTRAINT);

                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.LEFT, answer.getId(), ConstraintSet.RIGHT, 8);
                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, basicMargin);
                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 20);

                                    constraintSet.connect(answer.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, basicMargin);
                                    constraintSet.connect(answer.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 350);
                                    constraintSet.connect(answer.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 20);

                                } else if(answerType.equals("self")){
                                    dropAnswer.setText("");
                                    dropAnswer.setHint("관계");
                                    dropAnswer.setVisibility(View.GONE);
                                    Amanager.crossFade(dropAnswer,true);

                                    constraintSet.constrainHeight(dropAnswer.getId(), ConstraintSet.WRAP_CONTENT);
                                    constraintSet.constrainWidth(dropAnswer.getId(), ConstraintSet.MATCH_CONSTRAINT);

                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.LEFT, answer.getId(), ConstraintSet.RIGHT, 8);
                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, basicMargin);
                                    constraintSet.connect(dropAnswer.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 20);

                                    constraintSet.connect(answer.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, basicMargin);
                                    constraintSet.connect(answer.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 350);
                                    constraintSet.connect(answer.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 20);

                                } else{
                                    constraintSet.connect(answer.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, basicMargin);
                                    constraintSet.connect(answer.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, basicMargin);
                                    constraintSet.connect(answer.getId(), ConstraintSet.TOP, contMain.getId(), ConstraintSet.BOTTOM, 20);
                                }
                            }
                        }

                        constraintSet.applyTo(layout);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        stopService(service);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(service);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(service);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(service);
    }
}
