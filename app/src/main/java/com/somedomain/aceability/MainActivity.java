package com.somedomain.aceability;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//TODO change toast color/position so it's not on top of keyboard

public class MainActivity extends AppCompatActivity {
    int startNum = 1, endNum = 5, numOfNum=4, numOfBoxes=6, toolbarHeight; // constants for game play
    int mintRightPandN, mintRightNum, mintRightPos, mintMissCount;
    boolean isStartUp = true;
    List<EditText> maxBoxes, inputBoxes;
    TextView tvRightPandN, tvRightNum, tvRightPos, tvMissCounter, tvYouWIN, tvFinalAnswer, tvTimePassed;
    Button mButtonTryYourLuck;
    Integer [] currentCode, playerGuess;
    Spinner mSpinnerBoxes, mSpinnerNumMax;
    Timer mTimer;
    Calendar startCalendar;
    static Handler uiHandler;
    static int TIME_CODE = 1;
    LinearLayout mLinLayGameInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar_mainActivity);
        toolbar.setTitle("");
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbarHeight = toolbar.getBottom();
            }
        });

        uiHandler = new MyWeakHandler(this);

        View ll = getLayoutInflater().inflate(R.layout.linearlayout_for_toolbar, null);
        Toolbar.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = 0;
        ll.setLayoutParams(params);
        toolbar.addView(ll);
        setSupportActionBar(toolbar);

        mSpinnerBoxes = ll.findViewById(R.id.spinner_maxBox);
        String[] boxNums = {"6","7","8","9","10","11","12"};
        final ArrayAdapter<CharSequence> boxAdapter = new ArrayAdapter<CharSequence>(this, R.layout.my_spinner_simple, boxNums);
        boxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBoxes.setAdapter(boxAdapter);
        mSpinnerBoxes.setSelection(0);
        mSpinnerBoxes.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numOfBoxes = Integer.valueOf(boxAdapter.getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSpinnerNumMax = ll.findViewById(R.id.spinner_maxNum);
        String[] maxNumSelec = {"2","3","4","5","6","7","8","9"};
        final ArrayAdapter<CharSequence> numMaxAdapter = new ArrayAdapter<CharSequence>(this, R.layout.my_spinner_simple, maxNumSelec);
        numMaxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerNumMax.setAdapter(numMaxAdapter);
        mSpinnerNumMax.setSelection(numMaxAdapter.getPosition("4"));
        mSpinnerNumMax.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endNum = Integer.valueOf(numMaxAdapter.getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        initializeBoxes();

        tvTimePassed = findViewById(R.id.textView_timeElapsed);
        tvRightPandN = findViewById(R.id.textView_bothCorrect);
        tvRightNum = findViewById(R.id.textView_onlyNumRight);
        tvRightPos = findViewById(R.id.textView_onlyPosRight);
        tvMissCounter = findViewById(R.id.textView_missCounter);
        tvYouWIN = findViewById(R.id.textView_youGotIt);
        tvFinalAnswer = findViewById(R.id.textView_correctAnswer);
        mButtonTryYourLuck = findViewById(R.id.button_makeAGuess);
        mButtonTryYourLuck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(MainActivity.this);
                checkUserCode();
            }
        });

        mLinLayGameInfo = findViewById(R.id.linearLayout_gameInfo);
        mLinLayGameInfo.findViewById(R.id.button_gameInfoDone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinLayGameInfo.setVisibility(View.GONE);
            }
        });


        gameOverMan(false);


    }


    InputFilter lengthFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = "";

            if (start==dstart && end==dend) {
                return  source;
            }

            if (dest.length()==1 && !source.toString().equals("")) {
                makeCustomToast("1桁のみ");
            }

            if (dend == 0) {
                out= source;
            }
            return out;

        }
    };
    InputFilter rangeFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int intRep=-1;
            CharSequence out = "";
            try {
                intRep = Integer.valueOf(source.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "";

            }
            if (intRep > endNum || intRep < startNum) {
                out = "";
                makeCustomToast(startNum + "から" + endNum + "まで");
            } else {
                out = String.valueOf(intRep);
            }
            return out;
        }
    };
    InputFilter[] filters = {lengthFilter,rangeFilter};

    void initializeBoxes() {
        maxBoxes = new ArrayList<>();
        maxBoxes.add((EditText)findViewById(R.id.editText_index0));
        maxBoxes.add((EditText)findViewById(R.id.editText_index1));
        maxBoxes.add((EditText)findViewById(R.id.editText_index2));
        maxBoxes.add((EditText)findViewById(R.id.editText_index3));
        maxBoxes.add((EditText)findViewById(R.id.editText_index4));
        maxBoxes.add((EditText)findViewById(R.id.editText_index5));
        maxBoxes.add((EditText)findViewById(R.id.editText_index6));
        maxBoxes.add((EditText)findViewById(R.id.editText_index7));
        maxBoxes.add((EditText)findViewById(R.id.editText_index8));
        maxBoxes.add((EditText)findViewById(R.id.editText_index9));
        maxBoxes.add((EditText)findViewById(R.id.editText_index10));
        maxBoxes.add((EditText)findViewById(R.id.editText_index11));

        for (EditText et : maxBoxes) {
            et.setFilters(filters);

            et.setBackground(Util.makeSquare(et.getWidth(),et.getHeight(),getResources().getDisplayMetrics()));
        }
        inputBoxes = new ArrayList<>();

    }

    void startGame() {
        //hide unused EditText
        if (numOfBoxes!=12) {
            for (int i = 11; i >= numOfBoxes; i--) {
                maxBoxes.get(i).setVisibility(View.INVISIBLE);
            }
        }
        inputBoxes = maxBoxes.subList(0, numOfBoxes);
        playerGuess = new Integer[numOfBoxes];


        mSpinnerBoxes.setEnabled(false);
        mSpinnerNumMax.setEnabled(false);

        for (EditText et : inputBoxes) {
            et.setText("");
            et.setEnabled(true);
        }
        tvFinalAnswer.setVisibility(View.INVISIBLE);
        tvYouWIN.setVisibility(View.INVISIBLE);
        tvRightPos.setText("");
        tvRightNum.setText("");
        tvRightPandN.setText("");
        tvMissCounter.setText("");
        mintMissCount = 0;
        tvMissCounter.setText(String.valueOf(mintMissCount));
        currentCode = generateHiddenCode();
        String finalAnswer ="";
        for (int i = 0; i < currentCode.length; i++) {
            if (currentCode[i] == null) {
                finalAnswer += "X, ";
            } else {
                finalAnswer += currentCode[i] + ", ";
            }
        }
        finalAnswer = finalAnswer.substring(0, finalAnswer.lastIndexOf(","));
        tvFinalAnswer.setText(finalAnswer);
        mButtonTryYourLuck.setEnabled(true);
        //tvFinalAnswer.setVisibility(View.VISIBLE); //ONly used for testing
        startTimer();
    }

    void gameOverMan(boolean giveUp) {
        for (EditText et : maxBoxes) {
            //et.setText("");
            et.setEnabled(false);
            et.setVisibility(View.VISIBLE);
        }
        mSpinnerNumMax.setEnabled(true);
        mSpinnerBoxes.setEnabled(true);
        mButtonTryYourLuck.setEnabled(false);
        if (giveUp) {
            tvYouWIN.setText(R.string.you_lose);
        } else {
            tvYouWIN.setText(R.string.you_win);
        }
        if (isStartUp) {
            isStartUp = false;
        } else {
            tvYouWIN.setVisibility(View.VISIBLE);
            tvFinalAnswer.setVisibility(View.VISIBLE);
        }
        startCalendar = null;
        if (mTimer!=null) {
            mTimer.cancel();
        }



    }

    Integer[] generateHiddenCode() {
        Integer[] secreteCode = new Integer[numOfBoxes];
        int range = endNum-startNum+1;
        for (int i=0; i<numOfNum; i++) {
            int genNum = (int) (Math.random() * range);
            genNum +=startNum; // now we have a num in our range that is random ***** can be multiple of same num

            //check for free space

            boolean canEnterSpace = false;
            while (!canEnterSpace) {
                int spaceIndex = (int) (Math.random()* numOfBoxes);
                if (secreteCode[spaceIndex] == null) {
                    secreteCode[spaceIndex] = genNum;
                    canEnterSpace = true;
                }

            }
        }
        return secreteCode;
    }

    void checkUserCode() {
        //grab # from editText, make sure count is exactly numOfnum
        List<Integer> testList4Num = new ArrayList<>();
        for (EditText et : inputBoxes) {
            if (et!=null && !(et.getText().toString().equals(""))) {
                testList4Num.add(Integer.valueOf(et.getText().toString()));
            }

        }
        if ((testList4Num.size())!=numOfNum) {
            makeCustomToast("4つの位置に数字を入れてください");
            return;
        }
        // Should hav passed right number test and filters should mean that everything is single digit and within range, but need testing //TODO
        mintRightPandN = 0; mintRightNum = 0; mintRightPos = 0;


        // Create player array
        //playerGuess = new Integer[numOfBoxes];  //TODO double entry not needed?
        for (int i = 0; i < inputBoxes.size(); i++) {
            String s = inputBoxes.get(i).getText().toString();
            if (s == null || s.replace(" ","").equals("")) {
                playerGuess[i] = null;
            } else {
                playerGuess[i] = Integer.valueOf(s);
            }
        }
        // Both strings should be ready for comparison

        // Checking for correct position and then position and number
        for (int i = 0; i < inputBoxes.size(); i++) {

            //TODO add continue in first if() since if not right position will not be right pos/num
            if (playerGuess[i]!=null && currentCode[i]!=null) {
                mintRightPos++;
            }
            //first check for right position and num
            if ((playerGuess[i]!=null && currentCode[i]!=null) && (playerGuess[i].intValue()==currentCode[i].intValue())) {
                //right pos&num
                mintRightPandN ++;
            }

        }

        //last check will destroy play array
        for (int i = 0; i < currentCode.length; i++) {
            for (int k = 0; k < currentCode.length; k++) {
                if ( (currentCode[i]!=null && playerGuess[k]!=null) && (currentCode[i].intValue()==playerGuess[k].intValue())) {
                    mintRightNum++;
                    playerGuess[k] =null;
                    break;
                }
            }

        }
        tvRightPandN.setText(String.valueOf(mintRightPandN));
        tvRightNum.setText(String.valueOf(mintRightNum));
        tvRightPos.setText(String.valueOf(mintRightPos));

        if (mintRightPandN == numOfNum) {
            gameOverMan(false);
            return;
        }
        mintMissCount++;
        tvMissCounter.setText(String.valueOf(mintMissCount));
    }

    static final DateFormat sdf = new SimpleDateFormat("mm:ss");

    void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

//                //  - single Calendar approach
//                // Single calendar approach is cleaner but seems to be slightly less accurate over time. The process isn't instantaneous so slowly milliseconds are lost.
//                // getting the actual system time and subtracting it makes up for any lag in message queue etc...
//                // during a game the player would only lose maybe 1-5 seconds depending on difficulty
//                if (startCalendar != null) {
//                    startCalendar.add(Calendar.SECOND, 1);
//                } else {
//                    startCalendar = Calendar.getInstance();
//                    startCalendar.clear();
//                }
//
//                Message msg = uiHandler.obtainMessage();
//                msg.what = TIME_CODE;
//                Bundle b = new Bundle();
//                b.putString("time", sdf.format(startCalendar.getTime()));
//                msg.setData(b);
//                uiHandler.sendMessage(msg);


                Calendar c = null;
                if (startCalendar == null) {
                    startCalendar = Calendar.getInstance();
                } else {
                    c = Calendar.getInstance();
                }
                Message msg = uiHandler.obtainMessage();
                msg.what = TIME_CODE;
                Bundle b = new Bundle();

                if (c != null) {
                    long difference = c.getTimeInMillis() - startCalendar.getTimeInMillis();
                    //  all of these work but and seem to be equally accurate except for 2, no need to create new calendar,
                    //  3, similar to 1 calendar method but probably more accurate
                    //  also this can be done with 1 calendar

                    //1
                    c.setTimeInMillis(difference);
                    String s = sdf.format(c.getTime());
                    b.putString("time", s);

                    //2
//                    Calendar seconds = Calendar.getInstance();
//                    seconds.setTimeInMillis(difference);
//                    String s = sdf.format(seconds.getTime());
//                    b.putString("time", s);


                    //3
//                    c.clear();
//                    c.set(Calendar.SECOND, Math.round(difference/1000));
//                    String s = sdf.format(c.getTime());
//                    b.putString("time", s);

                } else {
                    b.putString("time", "00:00");
                }

                msg.setData(b);
                uiHandler.sendMessage(msg);

            }
        }, 0, 1000);
    }

    static class MyWeakHandler extends Handler {
        private final WeakReference<MainActivity> mMainActivityWeakReference;

        MyWeakHandler(MainActivity activity) {
            mMainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mAct = mMainActivityWeakReference.get();
            if (mAct != null) {
                //we cn now process messages on UI thread
                if (msg.what == TIME_CODE) {
                    mAct.tvTimePassed.setText(msg.getData().getString("time"));
                }

            }

        }

    }

    Toast bread;
    void makeCustomToast(String msg) {
        if (bread==null) {
            bread = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
            int dps = getResources().getDisplayMetrics().densityDpi;
//            int yOff = (int)(200/(getResources().getDisplayMetrics().density));
            bread.setGravity(Gravity.TOP, 0, (int) (toolbarHeight*1.5)); //TODO set
            View breadView = bread.getView();
            breadView.setBackgroundColor(getResources().getColor(R.color.toastBackground));
            TextView tv = breadView.findViewById(android.R.id.message);
            tv.setTextColor(getResources().getColor(R.color.toastTextColor));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            bread.show();
        }
        bread.setText(msg);
        bread.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_give_up) {
            gameOverMan(true);
            return true;
        }
        if (id == R.id.action_start) {
            startGame();
            return true;
        }
        if (id == R.id.action_game_info) {
            if (mLinLayGameInfo.getVisibility() != View.GONE) {
                mLinLayGameInfo.setVisibility(View.GONE);
            } else {
                mLinLayGameInfo.setVisibility(View.VISIBLE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
