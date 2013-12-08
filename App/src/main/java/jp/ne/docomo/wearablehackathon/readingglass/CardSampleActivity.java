package jp.ne.docomo.wearablehackathon.readingglass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class CardSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Card> cards = createCards();
        CardScrollView cardScrollView = new CardScrollView(this);
        ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter(cards);
        cardScrollView.setAdapter(adapter);
        cardScrollView.activate();
        setContentView(cardScrollView);
    }

    private List<Card> createCards() {
        List<Card> mCards = new ArrayList<Card>();

        Card card;

        card = new Card(this);
        card.setText("This card has a footer.");
        card.setInfo("I'm the footer!");
        mCards.add(card);

        card = new Card(this);
        card.setText("This card has a puppy background image.");
        card.setInfo("How can you resist?");
        card.setFullScreenImages(true);
        card.addImage(R.drawable.neco0);
        mCards.add(card);

        card = new Card(this);
        card.setText("This card has a mosaic of puppies.");
        card.setInfo("Aren't they precious?");
        card.addImage(R.drawable.neco1);
        card.addImage(R.drawable.neco2);
        card.addImage(R.drawable.neco3);
        mCards.add(card);

        return mCards;
    }

    private class ExampleCardScrollAdapter extends CardScrollAdapter {
        List<Card> mCards;

        public ExampleCardScrollAdapter(List<Card> cards) {
            mCards = cards;
        }

        @Override
        public int findIdPosition(Object id) {
            return -1;
        }

        @Override
        public int findItemPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).toView();
        }
    }
}