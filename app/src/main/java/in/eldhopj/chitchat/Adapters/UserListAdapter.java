package in.eldhopj.chitchat.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.eldhopj.chitchat.ModelClass.ListUser;
import in.eldhopj.chitchat.R;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> { //UserListAdapter


    private List<ListUser> mListItems; // List
    private Context mContext;
    private OnItemClickListener mListener; // Listener for the OnItemClickListener interface

    //constructor
    public UserListAdapter(List<ListUser> listItems, Context context) { // constructor
        this.mListItems = listItems;
        this.mContext = context;
    }

    /*
     * interface will forward our click from adapter to our main activity
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// this method calls when ever our view method is created , ie; the instance of ViewHolder class is created
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user, parent, false); /**list_item-> is the Card view which holds the data in the recycler view*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//populate the data into the list_item (View Holder), as we scroll
        //Binding data to the list_item
        ListUser listitem = mListItems.get(position);
        holder.nameTv.setText(listitem.getName());
        holder.phoneTv.setText(listitem.getPhone());

        if (listitem.getProfileImageUrl() != null)
        Picasso.get().load(listitem.getProfileImageUrl()).into(holder.profilePic);
    }

    @Override
    public int getItemCount() { // return the size of the list view , NOTE : this must be a fast process
        if (mListItems == null) {
            return 0;
        }
        return mListItems.size();
    }

    //View Holder class caches these references that gonna modify in the adapter
    class ViewHolder extends RecyclerView.ViewHolder{
        //Define viewHolder views (list_item) here
        TextView nameTv;
        TextView phoneTv;
        CircleImageView profilePic;

        //create a constructor with itemView as a params
        ViewHolder(View itemView) { // with the help of "itemView" we ge the views from xml
            super(itemView);
            //bind views
            nameTv = itemView.findViewById(R.id.name);
            phoneTv = itemView.findViewById(R.id.phoneNo);
            profilePic = itemView.findViewById(R.id.profilePic);

            //Assigning on click listener on the item
            itemView.setOnClickListener(new View.OnClickListener() { // we can handle the click as like we do in normal
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition(); // Get the index of the view holder
                        if (position != RecyclerView.NO_POSITION) { // Makes sure this position is still valid
                            mListener.onItemClick(v,position); // we catch the click on the item view then pass it over the interface and then to our activity
                        }
                    }

                }
            });
        }
    }
}
