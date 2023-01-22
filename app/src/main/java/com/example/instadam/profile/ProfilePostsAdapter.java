package com.example.instadam.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.components.Post;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProfilePostsAdapter allows to list the pictures taken
 * by the user of a 2 columns GridView.
 */
public class ProfilePostsAdapter extends BaseAdapter {

    private final List<Post> listPost;
    private final LayoutInflater layoutInflater;
    private final Context context;

    /**
     * Called when the register button is pressed.
     *
     * Redirects to our login page if all is correct.
     * Otherwise displays an error message
     *
     * @param context  The context of the caller.
     * @param listPost  The list of photos of the user.
     */
    public ProfilePostsAdapter(Context context,  List<Post> listPost) {
        Log.d("ProfilePostsAdapter: ", "constructor(" + context + ", " + listPost + ")");

        this.context = context;
        this.listPost = listPost;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listPost.size();
    }

    @Override
    public Object getItem(int position) {
        return listPost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Set a post on the GridView
     *
     * @param position The position of the post in the list.
     * @param convertView  The view.
     * @param parent  The parent.
     *
     * @return layoutItem
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("ProfilePostsAdapter: ", "getView(" + position + ", " + convertView + ", " + parent + ")");

        ConstraintLayout layoutItem;

        if (convertView == null) {
            layoutItem = (ConstraintLayout) this.layoutInflater.inflate(R.layout.grid_post_layout, parent, false);
        } else {
            layoutItem = (ConstraintLayout) convertView;
        }

        ImageView photo = layoutItem.findViewById(R.id.photo);

        photo.setImageBitmap(listPost.get(position).getImage());

        photo.setTag(position);
        photo.setOnClickListener(click -> printAlertDialogDelete(position));

        return layoutItem;
    }

    /**
     * Called when a photo is pressed.
     *
     * Show an alertDialog for delete the photo
     *
     * @param position The position of the post in the list.
     *
     */
    public void printAlertDialogDelete(int position) {
        Log.d("ProfilePostsAdapter: ", "printAlertDialogDelete(" + position + ")");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Voulez-vous supprimer cette photo ?");
        builder.setMessage(listPost.get(position).getName());
        builder.setNegativeButton("ANNULER", null);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(click -> deletePhoto(dialog, position));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(click -> dialog.cancel());
    }

    /**
     * Called when the button OK of the alertDialog is pressed.
     *
     * Delete the phtoto in the DataBase
     *
     * @param dialog The dialog for remove it after delete photo.
     * @param position The position of the post in the list.
     *
     */
    public void deletePhoto(AlertDialog dialog, int position) {
        Log.d("ProfilePostsAdapter: ", "deletePhoto(" + dialog + ", " + position + ")");

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        HTTPRequest request = new HTTPRequest(queue, context.getString(R.string.API_URL), User.getInstance(context).getAccessToken());

        request.makeRequest(Request.Method.DELETE, "/v1/images/" + listPost.get(position).getId(), headers, body, response -> {
                    Log.d("ProfilePostsAdapter: ", "deletePhotos() -> rqstDelete deleteImage OK");

                    listPost.remove(position);
                    notifyDataSetChanged();
                    dialog.cancel();
                }, error -> {
                    Log.e("ProfilePostsAdapter: ", "deletePhotos() -> rqstDelete deleteImage NOT OK" + error);

                    dialog.setTitle("Oups, il y a eu un problème.");
                    dialog.setMessage("");

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(click -> dialog.cancel());
                }
        );
    }

}
