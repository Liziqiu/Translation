package rongim.gdut.com.translation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gdut.http.Translation;

import java.util.List;

/**
 * Created by zhiqiang on 2015/12/7.
 */
public class SentenceAdapter extends BaseAdapter{

    private Context context;
    private List<Translation.Entry> data;

    public SentenceAdapter(Context context) {
        this.context = context;
        //data = new ArrayList<Translation.Entry>();
    }
    public void setData(List<Translation.Entry> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data == null)return 0;
        return data.size();
    }

    @Override
    public Translation.Entry getItem(int position) {
        if(data == null)return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.sentence_item,null);
            holder = new Holder();
            holder.source = (TextView) convertView.findViewById(R.id.source_sentence);
            holder.translated = (TextView) convertView.findViewById(R.id.translated_sentence);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.source.setText(getItem(position).source);
        holder.translated.setText(getItem(position).translated);
        return convertView;
    }

    class Holder{
        public TextView source;
        public TextView translated;
    }
}
