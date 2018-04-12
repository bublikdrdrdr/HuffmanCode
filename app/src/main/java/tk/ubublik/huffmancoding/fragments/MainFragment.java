package tk.ubublik.huffmancoding.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import tk.ubublik.huffmancoding.AppUtils;
import tk.ubublik.huffmancoding.CharStringPair;
import tk.ubublik.huffmancoding.HuffmanTreeLogic;
import tk.ubublik.huffmancoding.R;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Pair;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MainFragment extends Fragment {

    @ColorInt
    private static final int[] colors = new int[]{
            0xFFFF964C,
            0xFF70C3D8,
            0xFFFFF98C,
            0xFF4995FF,
            0xFF8EFF92,
            0xFFFBB5FF,
            0xFFC6D899
    };

    private HuffmanTreeLogic logic;
    private static final String logicKey = "logicKey";

    public MainFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            logic = AppUtils.valueOrDefault(logic, savedInstanceState.getParcelable(logicKey));
        } else {
            logic = new HuffmanTreeLogic(Character.SIZE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initTextInput(view);
        initCheckBox(view);
        view.findViewById(R.id.clearButton).setOnClickListener(v -> ((TextInputEditText)getView().findViewById(R.id.inputEditText)).setText(""));
        showEncodedData(view, coloredMode(view));
        showStats(view, logic.getStats());
        setCopyListener(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(logicKey, logic);
    }

    private void setCopyListener(View view){
        view.findViewById(R.id.encodedDataTextView).setOnLongClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.copiedMessage), Toast.LENGTH_SHORT).show();
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            String string = ((TextView)v).getText().toString();
            if (clipboard==null) return false;
            clipboard.setPrimaryClip(ClipData.newPlainText("Encoded data", string));
            return true;
        });
    }

    public HuffmanTree getTree(HuffmanTree.HuffmanTreeMode treeMode) {
        return logic.getTree(treeMode);
    }

    private void initCheckBox(View view){
        CheckBox checkBox = view.findViewById(R.id.showCodeAssociationCheckBox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> showEncodedData(getView(), isChecked));
    }

    private void initTextInput(View view){
        TextInputEditText textInputEditText = view.findViewById(R.id.inputEditText);
        textInputEditText.setText(logic.getLastInputData());
        textInputEditText.addTextChangedListener(inputTextWatcher);
    }

    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        private boolean ignoreTextWatcher = false;
        @Override
        @SuppressWarnings("unchecked")
        public void afterTextChanged(Editable s) {
            if (ignoreTextWatcher || s.length()==logic.getLastInputData().length()) return;
            if (s.length()==0){
                logic.clearData();
                ((TextInputLayout)getView().findViewById(R.id.inputEditTextLayout)).setError(null);
            } else if (s.length() > logic.getLastInputData().length() && logic.getLastInputData().contentEquals(s.subSequence(0, logic.getLastInputData().length()))){
                int from = logic.getLastInputData().length();
                logic.setInputData(s.toString());
                for (int i = from; i < s.length(); i++){
                    logic.send(s.charAt(i));
                }
                ((TextInputLayout)getView().findViewById(R.id.inputEditTextLayout)).setError(null);
            } else {
                ignoreTextWatcher = true;
                ((TextInputEditText)getView().findViewById(R.id.inputEditText)).setText(logic.getLastInputData());
                ignoreTextWatcher = false;
                String errorString;
                if (s.length()<logic.getLastInputData().length())
                    errorString = getString(R.string.inputBackspaceErrorMessage);
                else errorString = getString(R.string.inputInsertErrorMessage);
                ((TextInputLayout)getView().findViewById(R.id.inputEditTextLayout)).setError(errorString);
            }
            showStats(getView(), logic.getStats());
            showEncodedData(getView(), coloredMode(getView()));
        }
    };

    private boolean coloredMode(View view){
        return ((CheckBox)view.findViewById(R.id.showCodeAssociationCheckBox)).isChecked();
    }

    StatsTableBuilderAsyncTask asyncTask;
    private void showStats(View view, Map<HuffmanTreeLogic.StatsKey, String> map){
        try{
            asyncTask.cancel(true);
        } catch (Exception ignored){}
        asyncTask = new StatsTableBuilderAsyncTask(view);
        asyncTask.execute(map);
    }

    private void showEncodedData(View view, boolean colored){
        TextView encoded = view.findViewById(R.id.encodedDataTextView);
        TextView decoded = view.findViewById(R.id.decodedDataTextView);
        CharSequence decodedData = logic.getLastInputData();
        CharSequence encodedData = logic.getEncodedStringBuilder();
        if (colored) {
            encodedData = new SpannableString(encodedData);
            decodedData = new SpannableString(decodedData);
            int encodedCounter = 0;
            int decodedCounter = 0;
            for (CharStringPair pair : logic.getDataList()) {
                BackgroundColorSpan colorSpan = new BackgroundColorSpan(colors[decodedCounter%colors.length]);
                ((Spannable)encodedData).setSpan(colorSpan, encodedCounter, (encodedCounter += pair.second.length()), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ((Spannable)decodedData).setSpan(colorSpan, decodedCounter, ++decodedCounter, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        encoded.setText(encodedData);
        decoded.setText(decodedData);
    }

    private class StatsTableBuilderAsyncTask extends AsyncTask<Map<HuffmanTreeLogic.StatsKey, String>, Void, TableLayout> {

        View view;

        StatsTableBuilderAsyncTask(View view) {
            this.view = view;
        }

        @SafeVarargs
        @Override
        protected final TableLayout doInBackground(Map<HuffmanTreeLogic.StatsKey, String>... maps) {
            Map<HuffmanTreeLogic.StatsKey, String> map = maps[0];
            TableLayout tableLayout = new TableLayout(getContext());
            tableLayout.setStretchAllColumns(true);
            tableLayout.removeAllViews();
            for (Map.Entry<HuffmanTreeLogic.StatsKey, String> entry:map.entrySet()){
                if (entry.getValue()==null) continue;
                TableRow tableRow = new TableRow(getContext());
                TextView keyView = new TextView(getContext());
                TextView valueView = new TextView(getContext());
                keyView.setText(entry.getKey().getStringId());
                valueView.setText(entry.getValue());
                tableRow.addView(keyView);
                tableRow.addView(valueView);
                tableLayout.addView(tableRow);
            }
            return tableLayout;
        }

        @Override
        protected void onPostExecute(TableLayout tableLayout) {
            super.onPostExecute(tableLayout);
            view.post(() -> {
                FrameLayout frameLayout = view.findViewById(R.id.statsTableContainer);
                frameLayout.removeAllViews();
                frameLayout.addView(tableLayout);
            });
        }
    }

}
