package tk.ubublik.huffmancoding;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsRowViewHolder> {

    private List<StatsLeaf> list = new ArrayList<>();

    public void setList(List<StatsLeaf> list){
        if (list==null) return;
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public StatsRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatsRowViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_row, parent, false));
    }

    @Override
    public void onBindViewHolder(StatsRowViewHolder holder, int position) {
        StatsLeaf statsLeaf = list.get(position);
        holder.character.setText(AppUtils.nitableCharToString(statsLeaf.character));
        holder.charCode.setText(statsLeaf.character==null?"null":Integer.toString(statsLeaf.character));
        holder.count.setText(String.valueOf(statsLeaf.count));
        holder.probability.setText(String.format("%.2f%%", statsLeaf.probability*100));
        holder.code.setText(statsLeaf.code);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StatsRowViewHolder extends RecyclerView.ViewHolder{
        TextView character, charCode, count, probability, code;
        StatsRowViewHolder(View itemView) {
            super(itemView);
            character = itemView.findViewById(R.id.charTextView);
            charCode = itemView.findViewById(R.id.charNumberTextView);
            count = itemView.findViewById(R.id.countTextView);
            probability = itemView.findViewById(R.id.probabilityTextView);
            code = itemView.findViewById(R.id.codeTextView);
        }
    }
}
