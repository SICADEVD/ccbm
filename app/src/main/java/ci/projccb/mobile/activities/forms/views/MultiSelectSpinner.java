package ci.projccb.mobile.activities.forms.views;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ci.projccb.mobile.R;
import ci.projccb.mobile.tools.Commons;

public class MultiSelectSpinner extends AppCompatSpinner {

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

    @Override
    public boolean performClick() {


        ArrayList<SearchableItem> listModelNames = new ArrayList<>();
        ArrayList<Integer> listIndex = new ArrayList<>();
        for(int j = 0; j < _items.length; j++){
            listIndex.add(j);
            SearchableItem model = new SearchableItem(_items[j], String.valueOf(j));
            if(mSelection!=null) model.setSelected(mSelection[j]);
            listModelNames.add(model);
        }

        AlertDialog.Builder alertBd = new AlertDialog.Builder(getContext(), R.style.DialogTheme);

        Commons.Companion.adjustTextViewSizesInDialogExt(getContext(), alertBd, _title, getContext().getResources().getDimension(R.dimen._8ssp),true);
        SearchableMultiSelectSpinner.Companion.showDialog(getContext(), alertBd, _title, _titlePB, _titleNB, listModelNames, new SelectionCompleteListener() {

            public void onItemClicked(@NonNull SearchableItem selectedItem, int which, boolean checked) {
                if (mSelection != null && which < mSelection.length) {
                    if(hasNone) {
                        if (which == 0 && checked && mSelection.length > 1) {
                            for (int i = 1; i < mSelection.length; i++) {
                                mSelection[i] = false;
//                                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                            }
                        } else if (which > 0 && mSelection[0] && checked) {
                            mSelection[0] = false;
//                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                        }
                    }
                    mSelection[which] = checked;
                    simple_adapter.clear();
                    simple_adapter.add(buildSelectedItemString());
                } else {
                    throw new IllegalArgumentException(
                            "Argument 'which' is out of bounds.");
                }
            }

            public void onCancel(@NonNull ArrayList<SearchableItem> selectedItems) {
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);
                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
            }

            public void onCompleteSelection(@NonNull ArrayList<SearchableItem> selectedItems) {
                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                listener.selectedIndices(getSelectedIndices());
                listener.selectedStrings(getSelectedStrings());
            }
        });



        //AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        //builder.setTitle(_title);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_multichoice, Arrays.asList(_items));
//
//        final SearchView searchView = new SearchView(getContext());
//        searchView.setQueryHint("Rechercher…");
//        searchView.setIconifiedByDefault(false);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Filter items based on the search text
//                adapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Request focus for the SearchView
//                searchView.requestFocus();
//                // Show the keyboard
//                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            }
//        });
//
//        builder.setCustomTitle(searchView);


//        CustomMultiSearchAdapter adapter = new CustomMultiSearchAdapter(getContext(), Arrays.asList(_items), mSelection, new OnMultiDialClickListener(){
//
//            @Override
//            public void onCheckClick(int which, boolean checked) {
//                if (mSelection != null && which < mSelection.length) {
//                    if(hasNone) {
//                        if (which == 0 && checked && mSelection.length > 1) {
//                            for (int i = 1; i < mSelection.length; i++) {
//                                mSelection[i] = false;
////                                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
//                            }
//                        } else if (which > 0 && mSelection[0] && checked) {
//                            mSelection[0] = false;
////                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
//                        }
//                    }
//                    mSelection[which] = checked;
//                    simple_adapter.clear();
//                    simple_adapter.add(buildSelectedItemString());
//                } else {
//                    throw new IllegalArgumentException(
//                            "Argument 'which' is out of bounds.");
//                }
//            }
//        });
//
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        View v = inflater.inflate(R.layout.dialog_multi_searchlayout, null);
//        final SearchView searchView = (SearchView) v.findViewById(R.id.searchView);
//        final ListView listView = (ListView) v.findViewById(R.id.listView);
//
//        listView.setAdapter(adapter);

//        for(int j = 0; j < _items.length; j++){
//            for (int i = 0; i < simple_adapter.getCount(); i++) {
//                String item = simple_adapter.getItem(i);
//                if (item.contains(_items[j])) {
//                    adapter.
//                    break;
//                }
//            }
//        }

//        builder.setView(v);


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });

//        builder.setMultiChoiceItems(Arrays.asList(_items).toArray(new CharSequence[0]), mSelection, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean checked) {
//                if (mSelection != null && which < mSelection.length) {
//                    if(hasNone) {
//                        if (which == 0 && checked && mSelection.length > 1) {
//                            for (int i = 1; i < mSelection.length; i++) {
//                                mSelection[i] = false;
//                                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
//                            }
//                        } else if (which > 0 && mSelection[0] && checked) {
//                            mSelection[0] = false;
//                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
//                        }
//                    }
//                    mSelection[which] = checked;
//                    simple_adapter.clear();
//                    simple_adapter.add(buildSelectedItemString());
//                } else {
//                    throw new IllegalArgumentException(
//                            "Argument 'which' is out of bounds.");
//                }
//            }
//        });

        _itemsAtStart = getSelectedItemsAsString();
        if(_itemsAtStart == null) return true;
//        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setSelection(0);
//            }
//        });
//        builder.setPositiveButton(_titlePB, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
//                listener.selectedIndices(getSelectedIndices());
//                listener.selectedStrings(getSelectedStrings());
//            }
//        });
//        builder.setNegativeButton(_titleNB, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                simple_adapter.clear();
//                simple_adapter.add(_itemsAtStart);
//                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
//            }
//        });
//        builder.show();

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

        if(_items == null) return null;

        if(_items.length == 0) return null;

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
