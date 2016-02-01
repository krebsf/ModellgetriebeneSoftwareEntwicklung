package org.quasar.IceCream.utils;

import org.quasar.IceCream.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WarningDialogFragment extends DialogFragment {
	private String Title;
    private String Message;

    /**
     * Create a new instance of MyDialogFragment, providing "title" and "message" as arguments.
     */
    public static WarningDialogFragment newInstance(String title, String message) {
    	WarningDialogFragment f = new WarningDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("title", title);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Message = getArguments().getString("message");
        Title = getArguments().getString("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.default_warning_fragment, null);
        ((TextView) v.findViewById(R.id.warning_Title)).setText(Title);
        ((TextView) v.findViewById(R.id.warning_message)).setText(Message);
        builder.setNeutralButton("Close", null);
        builder.setView(v);

        Dialog dialog = builder.create();
        return dialog;
    }

}
