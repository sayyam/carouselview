<p align="center"><img src="/sample/src/main/assets/carousel_baner.jpg"></p>

CarouselView
=======
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CarouselView-green.svg?style=true)](https://android-arsenal.com/details/1/3289)

A simple yet flexible library to add carousel view in your android application.


<img src="/sample/src/main/assets/carousel_gif.gif" title="sample" width="500" height="460" />


Download
--------
####Gradle:
```groovy
compile 'com.synnapps:carouselview:0.0.7'
```
####Maven:
```xml
<dependency>
  <groupId>com.synnapps</groupId>
  <artifactId>carouselview</artifactId>
  <version>0.0.7</version>
  <type>pom</type>
</dependency>
```

Usage
--------

####Include following code in your layout:

```xml
    <com.synnapps.carouselview.CarouselView
        android:id="@+id/carouselView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:fillColor="#FFFFFFFF"
        app:pageColor="#00000000"
        app:radius="6dp"
        app:slideInterval="3000"
        app:strokeColor="#FF777777"
        app:strokeWidth="1dp"/>
```
####Include following code in your activity:
```java
public class SampleCarouselViewActivity extends AppCompatActivity {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_carousel_view);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

}
```

####If you want to add custom view, implement ```ViewListener```.
```java

public class SampleCarouselViewActivity extends AppCompatActivity {

    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_carousel_view);

        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        // set ViewListener for custom view 
        customCarouselView.setViewListener(viewListener);
    }

    ViewListener viewListener = new ViewListener() {
    
        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);
            //set view attributes here
            
            return customView;
        }
    };

```

####Supported xml Attributes

| Attribute          	                    | Description          							   			  		 | Values 				  |
| ------------------------------------------|--------------------------------------------------------------------|------------------------|
| app:slideInterval 	                    | Interval per page in milliseconds. 			   		      		 | integer				  |
| app:indicatorGravity                      | Gravity of the indicator.  (Just like layout_gravity) 			 | gravity                |
| app:indicatorOrientation                  | Orientation of the indicator. 					   			  	 | [horizontal, vertical] |
| app:fillColor	  		                    | Color of the filled circle that represents the current page. 		 | color 				  |
| app:pageColor   		                    | Color of the filled circles that represents pages. 		  		 | color 				  |
| app:radius 			                    | Radius of the circles. This is also the spacing between circles.   | dimension 			  |
| app:snap 				                    | Whether or not the selected indicator snaps to the circles. 		 | boolean 				  |
| app:strokeColor 		                    | Width of the stroke used to draw the circles. 					 | color 				  |
| app:autoPlay                              | Whether or not to auto play. Default: true                         | boolean                |
| app:disableAutoPlayOnUserInteraction      | Disables autoPlay when user interacts. Default: false              | boolean                |
| app:indicatorMarginHorizontal 			| Sets horizontal margin for Indicator in Carousel View              | dimension 			  |
| app:indicatorMarginVertical 			    | Sets vertical margin for Indicator in Carousel View                | dimension 			  |

_Note:_ Add ```xmlns:app="http://schemas.android.com/apk/res-auto"``` in your layout's root view.


Developed By
--------
- Sayyam Mehmood
- Muhammad Rehan

Special Thanks
--------

This library uses code snippet from Jake Wharton's [ViewPagerIndicator](https://github.com/JakeWharton/ViewPagerIndicator) to display page indicator.

License
--------

    Copyright 2016 Sayyam.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and 
    limitations under the License.
