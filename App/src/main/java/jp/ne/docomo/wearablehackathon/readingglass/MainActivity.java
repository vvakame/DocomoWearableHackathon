package jp.ne.docomo.wearablehackathon.readingglass;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Card card = new Card(this);
        card.setText("やばい");
        card.setInfo("すごい");

        setContentView(card.toView());
    }
}
