getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

if (scrollRange == -1) scrollRange = appBarLayout.getTotalScrollRange();

/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions transitionActivity =
                                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                        startActivity(intent, transitionActivity.toBundle());
                    } else*/

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
//        getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));


/** Capturing images */

/*  if (requestCode == ImageUtil.REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            avatarRequestBody = ImageUtil.getBitmapFromData(data, avatar);
        } else*/

/*@OnClick(R.id.capture_image_btn)
    public void cameraButtonClicked() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ImageUtil.REQUEST_CAPTURE_IMAGE);
        }
    }*/