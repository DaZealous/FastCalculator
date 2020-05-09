package utils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import www.mycompany.fastcalculator.R;

public class VideoTask extends AsyncTask {
    private Context context;

    VideoTask(Context context){
        this.context = context;
    }

    Dialog dialog = new Dialog(context);
    private ProgressBar progressBar;
    private TextView textProgress;

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_dialog_layout);

        progressBar = dialog.findViewById(R.id.progress_horizontal);
        textProgress = dialog.findViewById(R.id.text_progress);

        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        dialog.show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
