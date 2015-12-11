package rongim.gdut.com.translation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gdut.http.Text2Audio;
import com.gdut.http.Translation;
import com.gdut.http.httpError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText source_et;
    private EditText result_et;
    private Spinner from_sp;
    private Spinner to_sp;
    private Button translate_btn;
    private ListView sample_lv;
    private SentenceAdapter sentenceAdapter;

    private ImageView sourceSound;
    private ImageView resultSound;

    public static String TRANS_FEOM="zh";
    public static String TRANS_TO="en";
    public static String[] LanguageItem;
    private Translation tran;
    private Text2Audio txt2audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LanguageItem = getResources().getStringArray(R.array.languages_value);
        txt2audio = new Text2Audio(this.getCacheDir().getAbsolutePath());
        initView();
        tran = new Translation();
    }

    private void initView() {
        source_et = (EditText) this.findViewById(R.id.translate_source);
        result_et = (EditText) this.findViewById(R.id.translate_result);
        from_sp = (Spinner) this.findViewById(R.id.translate_from);
        to_sp = (Spinner) this.findViewById(R.id.translate_to);
        translate_btn = (Button) this.findViewById(R.id.do_translate);
        sample_lv = (ListView) this.findViewById(R.id.translate_sample_sentence);
        sourceSound = (ImageView) this.findViewById(R.id.translate_source_sound);
        resultSound = (ImageView) this.findViewById(R.id.translate_result_sound);

        sourceSound.setOnClickListener(this);
        resultSound.setOnClickListener(this);
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
                sample_lv.setVisibility(View.GONE);
                translate_btn.setVisibility(View.VISIBLE);
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
                sample_lv.setVisibility(View.GONE);
                translate_btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TRANS_TO = LanguageItem[2];
    }

    @Override
    public void onClick(View v) {
        if(v == translate_btn){
            if(source_et.getText() == null || source_et.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this,"需要翻译的内容为空",Toast.LENGTH_SHORT).show();
                return;
            }
            final Translation.Request request = new Translation.Request(TRANS_FEOM,TRANS_TO,source_et.getText().toString());
            tran.doTranslate(request, new Translation.CallBack() {
                @Override
                public void Completed(Translation.Responed responed) {
                    if (MainActivity.this.isFinishing())return;
                    result_et.setText(responed.result);
                    sentenceAdapter.setData(responed.sample);
                    sentenceAdapter.notifyDataSetChanged();
                    sample_lv.setVisibility(View.VISIBLE);
                    translate_btn.setVisibility(View.GONE);
                }

                @Override
                public void Error(httpError error) {
                    if (MainActivity.this.isFinishing())return;
                    Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else if(v == sourceSound){
            if(source_et.getText() == null || source_et.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this,"需要翻译的内容为空",Toast.LENGTH_SHORT).show();
                return;
            }
            asyncSound(TRANS_FEOM,source_et.getText().toString().trim());
            sourceSound.setEnabled(false);
            resultSound.setEnabled(false);
        }else if(v == resultSound){
            if(result_et.getText() == null || result_et.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this,"翻译的结果为空",Toast.LENGTH_SHORT).show();
                return;
            }
            asyncSound(TRANS_TO,result_et.getText().toString().trim());
            sourceSound.setEnabled(false);
            resultSound.setEnabled(false);
        }
    }
    private void asyncSound(final String lan, final String txt){
        txt2audio.AsyncGetAudio(lan, txt, new Text2Audio.CallBack() {
            @Override
            public void Completed(String audioPath) {
                if (MainActivity.this.isFinishing())return;
                txt2audio.playSound(audioPath, MainActivity.this);
                sourceSound.setEnabled(true);
                resultSound.setEnabled(true);
            }

            @Override
            public void Error(httpError error) {
                if (MainActivity.this.isFinishing())return;
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                sourceSound.setEnabled(true);
                resultSound.setEnabled(true);
            }
        });
    }
}
