package com.negiwang.android.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.negiwang.android.MainActivity;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

public class ExitPopup extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exit_popup);

        init();


    }

   public void init(){
       ImageView exitImg = (ImageView) findViewById((R.id.exitImg));


       Glide.with(ExitPopup.this)
               .load(M.getMEndImgSrc(ExitPopup.this))
               .into(exitImg);

       exitImg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               if (!M.getMEndImgTurl(ExitPopup.this).equals("")) {
                   if (M.getMEndImgBrwser(ExitPopup.this).equals("in")) {

                       Intent intent = new Intent(ExitPopup.this, MainActivity.class);

                       intent.putExtra("CALLBACK_URL", M.getMEndImgTurl(ExitPopup.this));

                       startActivity(intent);
                       finish();
                       overridePendingTransition(0,0);

                   } else {
                       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(M.getMEndImgTurl(ExitPopup.this)));
                       startActivity(browserIntent);
                       finish();
                   }
               } else {

               }

           }
       });
   }


}