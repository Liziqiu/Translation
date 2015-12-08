package rongim.gdut.com.translation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gdut.http.Translation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText source_et;
    private EditText result_et;
    private Spinner from_sp;
    private Spinner to_sp;
    private Button translate_btn;
    private ListView sample_lv;
    private SentenceAdapter sentenceAdapter;
    public static String TRANS_FEOM="zh";
    public static String TRANS_TO="en";
    public static String[] LanguageItem;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Translation.Responed responed = (Translation.Responed) msg.obj;
                    result_et.setText(responed.result);
                    sentenceAdapter.setData(responed.sample);
                    sentenceAdapter.notifyDataSetChanged();
                    sample_lv.setVisibility(View.VISIBLE);
                    translate_btn.setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LanguageItem = getResources().getStringArray(R.array.languages_value);
        initView();
    }

    private void initView() {
        source_et = (EditText) this.findViewById(R.id.translate_source);
        result_et = (EditText) this.findViewById(R.id.translate_result);
        from_sp = (Spinner) this.findViewById(R.id.translate_from);
        to_sp = (Spinner) this.findViewById(R.id.translate_to);
        translate_btn = (Button) this.findViewById(R.id.do_translate);
        sample_lv = (ListView) this.findViewById(R.id.translate_sample_sentence);
        translate_btn.setOnClickListener(this);
        sentenceAdapter = new SentenceAdapter(this);
        sample_lv.setAdapter(sentenceAdapter);

        source_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(translate_btn.getVisibility() == View.GONE){
                    translate_btn.setVisibility(View.VISIBLE);
                    sample_lv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(translate_btn.getVisibility() == View.GONE){
                    translate_btn.setVisibility(View.VISIBLE);
                    sample_lv.setVisibility(View.GONE);
                }
            }
        });

        from_sp.setSelection(1);
        from_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TRANS_FEOM = LanguageItem[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TRANS_FEOM = LanguageItem[1];
        to_sp.setSelection(2);
        to_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TRANS_TO = LanguageItem[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TRANS_TO = LanguageItem[2];
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == translate_btn){
            if(source_et.getText() == null || source_et.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this,"需要翻译的内容为空",Toast.LENGTH_SHORT).show();
                return;
            }
            final Translation.Request request = new Translation.Request(TRANS_FEOM,TRANS_TO,source_et.getText().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Translation.Responed responed = doTranslate(request);
                    mhandler.sendMessage(mhandler.obtainMessage(0,responed));
                }
            }).start();
        }
    }

    private Translation.Responed doTranslate(Translation.Request request) {
        return Translation.translate(request);
        //result_et.setText(responed.result);
       // sentenceAdapter.setData(responed.sample);
    }
}
