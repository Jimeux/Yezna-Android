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