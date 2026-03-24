package com.example.goout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * מסך המציג את המקומות שהמשתמש שמר במועדפים.
 * משתמש ב-RecyclerView ובעיצוב item_place_card.
 */
public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private List<FavoritePlace> favoriteList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results); // משתמשים באותו Layout של תוצאות כי הוא מכיל RecyclerView

        // עדכון כותרת המסך (במידה ויש TextView מתאים ב-Layout)
        TextView title = findViewById(R.id.results_title); // ודאי שיש ID כזה או השתמשי ב-Toolbar
        if (title != null) title.setText("המועדפים שלי");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        favoriteList = new ArrayList<>();

        recyclerView = findViewById(R.id.results_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoritesAdapter(favoriteList);
        recyclerView.setAdapter(adapter);

        loadFavoritesFromFirebase();

        // כפתור חזרה
        View btnBack = findViewById(R.id.btn_back_home);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadFavoritesFromFirebase() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String desc = document.getString("description");
                            favoriteList.add(new FavoritePlace(name, desc));
                        }
                        adapter.notifyDataSetChanged();
                        if (favoriteList.isEmpty()) {
                            Toast.makeText(this, "אין מועדפים שמורים", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // מודל נתונים פנימי
    static class FavoritePlace {
        String name, description;
        FavoritePlace(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    // אדפטר פנימי המשתמש ב-item_place_card
    class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private final List<FavoritePlace> list;

        FavoritesAdapter(List<FavoritePlace> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FavoritePlace p = list.get(position);
            holder.name.setText(p.name);
            holder.description.setText(p.description);
            // במועדפים הכוכב כבר דולק
            holder.favBtn.setImageResource(android.R.drawable.btn_star_big_on);

            // אפשר להוסיף כאן לוגיקה למחיקה בלחיצה חוזרת על הכוכב
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, description;
            ImageButton favBtn;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.item_place_name);
                description = v.findViewById(R.id.item_place_distance); // משתמשים בשדה התיאור
                favBtn = v.findViewById(R.id.item_fav_button);
            }
        }
    }
}