package com.jgraycar.represent;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Joel on 2/27/16.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SenatorViewHolder>{

    List<Senator> senators;
    ViewGroup parent;

    RVAdapter(List<Senator> senators){
        this.senators = senators;
    }

    @Override
    public int getItemCount() {
        return senators.size();
    }

    @Override
    public SenatorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.parent = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.senator_card_view, viewGroup, false);
        SenatorViewHolder senatorViewHolder = new SenatorViewHolder(v);
        return senatorViewHolder;
    }

    @Override
    public void onBindViewHolder(SenatorViewHolder senatorViewHolder, int i) {
        Senator senator = senators.get(i);

        Drawable[] layers = new Drawable[2];
        Bitmap bg = decodeSampledBitmapFromResource(parent.getResources(), senator.photoId, 155, 100);
        layers[0] = new BitmapDrawable(parent.getResources(), bg);
        layers[1] = ContextCompat.getDrawable(parent.getContext(), senator.partyIconId());
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        senatorViewHolder.senatorPhoto.setImageDrawable(layerDrawable);

        senatorViewHolder.senatorName.setText(senator.name);
        senatorViewHolder.senatorTerm.setText(senator.term);
        senatorViewHolder.senatorWebsite.setText(senator.website);
        senatorViewHolder.senatorEmail.setText(senator.email);
        senatorViewHolder.senatorTweet.setText(senator.tweet);

        senatorViewHolder.detailsButton.setTag(senator);
        senatorViewHolder.senatorPhoto.setTag(senator);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class SenatorViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senatorName;
        TextView senatorTerm;
        TextView senatorEmail;
        TextView senatorWebsite;
        TextView senatorTweet;
        ImageView senatorPhoto;
        Button detailsButton;

        SenatorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            senatorName = (TextView)itemView.findViewById(R.id.senator_name);
            senatorTerm = (TextView)itemView.findViewById(R.id.senator_term);
            senatorPhoto = (ImageView)itemView.findViewById(R.id.senator_photo);
            senatorEmail = (TextView)itemView.findViewById(R.id.senator_email);
            senatorWebsite = (TextView)itemView.findViewById(R.id.senator_website);
            senatorTweet = (TextView)itemView.findViewById(R.id.senator_tweet);
            detailsButton = (Button)itemView.findViewById(R.id.detailsButton);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}