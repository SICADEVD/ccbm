package ci.projccb.mobile.activities.forms.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ci.projccb.mobile.R;
import ci.projccb.mobile.tools.Commons;

public class MultiSelectSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {
    public interface OnMultipleItemsSelectedListener{
        void selectedIndices(List<Integer> indices);
        void selectedStrings(List<String> strings);
    }
    private MultiSelectSpinner.OnMultipleItemsSelectedListener listener;

    String[] _items = null;
    String _title = "Pas de titre", _titlePB = "Ajouter", _titleNB = "Fermer";
    boolean[] mSelection = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;
    Context c;
    ArrayAdapter<String> simple_adapter;
    private boolean hasNone = false;

    public MultiSelectSpinner(Context context) {
        super(context);
        c = context;
        simple_adapter = new ArrayAdapter<>(context,
                R.layout.simple_spin_multi);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        //View view = android.R.layout.simple_spinner_item;
        simple_adapter = new ArrayAdapter<>(context, R.layout.simple_spin_multi);
        super.setAdapter(simple_adapter);
    }

    public void setListener(MultiSelectSpinner.OnMultipleItemsSelectedListener listener){
        this.listener = listener;
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            if(hasNone) {
                if (which == 0 && isChecked && mSelection.length > 1) {
                    for (int i = 1; i < mSelection.length; i++) {
                        mSelection[i] = false;
                        ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                    }
                } else if (which > 0 && mSelection[0] && isChecked) {
                    mSelection[0] = false;
                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                }
            }
            mSelection[which] = isChecked;
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        Commons.Companion.adjustTextViewSizesInDialogExt(getContext(), builder, _title, getContext().getResources().getDimension(R.dimen._8ssp),true);
        //builder.setTitle(_title);
        builder.setMultiChoiceItems(_items, mSelection, this);
        _itemsAtStart = getSelectedItemsAsString();
//        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setSelection(0);
//            }
//        });
        builder.setPositiveButton(_titlePB, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                listener.selectedIndices(getSelectedIndices());
                listener.selectedStrings(getSelectedStrings());
            }
        });
        builder.setNegativeButton(_titleNB, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);
                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setPositiveButtonTitle(String title) {
        _titlePB = title;
    }
    public void setNegativeButtonTitle(String title) {
        _titleNB = title;
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(List<String> items) {
        if(items.size() > 0){
            _items = items.toArray(new String[items.size()]);
            mSelection = new boolean[_items.length];
            mSelectionAtStart  = new boolean[_items.length];
        }else {
            _items = new String[]{};
            mSelection = new boolean[]{};
            mSelectionAtStart = new boolean[]{};
        }
        simple_adapter.clear();
        if(items.size() > 0) {
            simple_adapter.add(_items[0]);
            Arrays.fill(mSelection, false);
            mSelection[0] = true;
        }
    }

    public void setSelection(String[] selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }


    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
            mSelectionAtStart[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (int index : selectedIndices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public void hasNoneOption(boolean val){
        hasNone = val;
    }
}
