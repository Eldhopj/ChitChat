package in.eldhopj.chitchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import in.eldhopj.chitchat.Adapters.UserListAdapter;
import in.eldhopj.chitchat.ModelClass.ListUserModelClass;

public class FindUserActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<ListUserModelClass> mListItems;
    private UserListAdapter mUserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        mRecyclerView = findViewById(R.id.userList);
        mRecyclerView.setHasFixedSize(true); // setting it to true allows some optimization to our view , avoiding validations when mUserListAdapter content changes

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //it can be GridLayoutManager or StaggeredGridLayoutManager

        //set the mUserListAdapter to the recycler view
        mUserListAdapter = new UserListAdapter(mListItems, this);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)); // Divider decorations
        mRecyclerView.setAdapter(mUserListAdapter);
    }
}
