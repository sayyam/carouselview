package com.synnapps.example.carouselview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

public class SampleCarouselViewActivity extends AppCompatActivity {

    CarouselView carouselView;
    CarouselView customCarouselView;

    TextView carouselLabel;
    TextView customCarouselLabel;

    Button pauseButton;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};
    String[] sampleTitles = {"Orange", "Grapes", "Strawberry"};
    String[] sampleNetworkImageURLs = {
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image1&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image2&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image3&txt=350%C3%97150&w=350&h=150"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_carousel_view);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        carouselLabel = (TextView) findViewById(R.id.carouselLabel);
        customCarouselLabel = (TextView) findViewById(R.id.customCarouselLabel);
        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            carouselView.pauseCarousel();
            customCarouselView.reSetSlideInterval(0);
          }
        });

        carouselView.setPageCount(sampleImages.length);
        carouselView.setSlideInterval(4000);

        customCarouselView.setPageCount(sampleImages.length);
        customCarouselView.setSlideInterval(4000);

        carouselView.setImageListener(imageListener);
        customCarouselView.setViewListener(viewListener);
    }

    // To set simple images
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {

            Picasso
                .with(getApplicationContext())
                .load(sampleNetworkImageURLs[position])
                .placeholder(sampleImages[0])
                .error(sampleImages[0])
                .resize(400, 200)
                .centerCrop()
                .into(imageView);

            //imageView.setImageResource(sampleImages[position]);
        }
    };

    // To set custom views
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.labelTextView);
            ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            fruitImageView.setImageResource(sampleImages[position]);
            labelTextView.setText(sampleTitles[position]);

            return customView;
        }
    };
}
