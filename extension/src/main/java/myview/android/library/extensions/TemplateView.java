package myview.android.library.extensions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import myview.android.library.extensions.R;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;


public class TemplateView extends FrameLayout {
    private static final String MEDIUM_TEMPLATE = "medium_template";
    private static final String SMALL_TEMPLATE = "small_template";
    private int templateType = R.layout.gnt_medium_template_view;
    private int templateMinHeight = 120;
    private NativeTemplateStyle styles;
    private NativeAd nativeAd;
    private NativeAdView nativeAdView;
    private android.widget.TextView primaryView;
    private android.widget.TextView secondaryView;
    private RatingBar ratingBar;
    private android.widget.TextView tertiaryView;
    private ImageView iconView;
    private MediaView mediaView;
    private Button callToActionView;
    private LinearLayout background;


    public TemplateView(Context context) {
        super(context);
    }

    public TemplateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TemplateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @SuppressLint("NewApi")
    public TemplateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    public void setStyles(NativeTemplateStyle styles) {
        this.styles = styles;
        this.applyStyles();
    }

    public NativeAdView getNativeAdView() {
        return nativeAdView;
    }

    private void applyStyles() {

        android.graphics.drawable.Drawable mainBackground = styles.getMainBackgroundColor();
        if (mainBackground != null) {
            background.setBackground(mainBackground);
            if (primaryView != null) {
                primaryView.setBackground(mainBackground);
            }
            if (secondaryView != null) {
                secondaryView.setBackground(mainBackground);
            }
            if (tertiaryView != null) {
                tertiaryView.setBackground(mainBackground);
            }
        }

        Typeface primary = styles.getPrimaryTextTypeface();
        if (primary != null && primaryView != null) {
            primaryView.setTypeface(primary);
        }

        Typeface secondary = styles.getSecondaryTextTypeface();
        if (secondary != null && secondaryView != null) {
            secondaryView.setTypeface(secondary);
        }

        Typeface tertiary = styles.getTertiaryTextTypeface();
        if (tertiary != null && tertiaryView != null) {
            tertiaryView.setTypeface(tertiary);
        }

        Typeface ctaTypeface = styles.getCallToActionTextTypeface();
        if (ctaTypeface != null && callToActionView != null) {
            callToActionView.setTypeface(ctaTypeface);
        }

        int primaryTypefaceColor = styles.getPrimaryTextTypefaceColor();
        if (primaryTypefaceColor > 0 && primaryView != null) {
            primaryView.setTextColor(primaryTypefaceColor);
        }

        int secondaryTypefaceColor = styles.getSecondaryTextTypefaceColor();
        if (secondaryTypefaceColor > 0 && secondaryView != null) {
            secondaryView.setTextColor(secondaryTypefaceColor);
        }

        int tertiaryTypefaceColor = styles.getTertiaryTextTypefaceColor();
        if (tertiaryTypefaceColor > 0 && tertiaryView != null) {
            tertiaryView.setTextColor(tertiaryTypefaceColor);
        }

        int ctaTypefaceColor = styles.getCallToActionTypefaceColor();
        if (ctaTypefaceColor > 0 && callToActionView != null) {
            callToActionView.setTextColor(ctaTypefaceColor);
        }

        float ctaTextSize = styles.getCallToActionTextSize();
        if (ctaTextSize > 0 && callToActionView != null) {
            callToActionView.setTextSize(ctaTextSize);
        }

        float primaryTextSize = styles.getPrimaryTextSize();
        if (primaryTextSize > 0 && primaryView != null) {
            primaryView.setTextSize(primaryTextSize);
        }

        float secondaryTextSize = styles.getSecondaryTextSize();
        if (secondaryTextSize > 0 && secondaryView != null) {
            secondaryView.setTextSize(secondaryTextSize);
        }

        float tertiaryTextSize = styles.getTertiaryTextSize();
        if (tertiaryTextSize > 0 && tertiaryView != null) {
            tertiaryView.setTextSize(tertiaryTextSize);
        }

        android.graphics.drawable.Drawable ctaBackground = styles.getCallToActionBackgroundColor();
        if (ctaBackground != null && callToActionView != null) {
            callToActionView.setBackground(ctaBackground);
        }

        android.graphics.drawable.Drawable primaryBackground = styles.getPrimaryTextBackgroundColor();
        if (primaryBackground != null && primaryView != null) {
            primaryView.setBackground(primaryBackground);
        }

        android.graphics.drawable.Drawable secondaryBackground = styles.getSecondaryTextBackgroundColor();
        if (secondaryBackground != null && secondaryView != null) {
            secondaryView.setBackground(secondaryBackground);
        }

        android.graphics.drawable.Drawable tertiaryBackground = styles.getTertiaryTextBackgroundColor();
        if (tertiaryBackground != null && tertiaryView != null) {
            tertiaryView.setBackground(tertiaryBackground);
        }

        invalidate();
        requestLayout();
    }

    private boolean adHasOnlyStore(NativeAd nativeAd) {
        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser);
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;

        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        String headline = nativeAd.getHeadline();
        String body = nativeAd.getBody();
        String cta = nativeAd.getCallToAction();
        Double starRating = nativeAd.getStarRating();
        NativeAd.Image icon = nativeAd.getIcon();

        String secondaryText;

        nativeAdView.setCallToActionView(callToActionView);
        nativeAdView.setHeadlineView(primaryView);
        nativeAdView.setMediaView(mediaView);
        secondaryView.setVisibility(VISIBLE);
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView.setStoreView(secondaryView);
            secondaryText = store;
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView.setAdvertiserView(secondaryView);
            secondaryText = advertiser;
        } else {
            secondaryText = "";
        }

        primaryView.setText(headline);
        callToActionView.setText(cta);

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView.setVisibility(GONE);
            ratingBar.setVisibility(GONE);
            ratingBar.setMax(5);
            nativeAdView.setStarRatingView(ratingBar);
        } else {
            secondaryView.setText(secondaryText);
            secondaryView.setVisibility(VISIBLE);
            ratingBar.setVisibility(GONE);
        }

        if (icon != null) {
            iconView.setVisibility(VISIBLE);
            iconView.setImageDrawable(icon.getDrawable());
        } else {
            iconView.setVisibility(INVISIBLE);
        }

        if (tertiaryView != null) {
            tertiaryView.setText(body);
            nativeAdView.setBodyView(tertiaryView);
        }
        updateBg();
        updateTextColor(primaryView);
        updateTextColor(tertiaryView);
        updateTextColor(secondaryView);
        nativeAdView.setNativeAd(nativeAd);
    }

    public void destroyNativeAd() {
        nativeAd.destroy();
    }

    public String getTemplateTypeName() {
        if (templateType == R.layout.gnt_medium_template_view) {
            return MEDIUM_TEMPLATE;
        } else if (templateType == R.layout.gnt_small_template_view) {
            return SMALL_TEMPLATE;
        }
        return "";
    }

    public int setTemplateTypeName(int _templateType) {
        if (_templateType == 0) {
            templateType = R.layout.gnt_medium_template_view;
        } else if (_templateType == 1) {
            templateType = R.layout.gnt_small_template_view;
        }
        return templateType;
    }

    private void initView(Context context, AttributeSet attributeSet) {

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0);

        try {
            templateType = attributes.getResourceId(R.styleable.TemplateView_gnt_template_type, templateType);
            templateMinHeight = attributes.getResourceId(R.styleable.TemplateView_gnt_template_min_height, templateMinHeight);
        } finally {
            attributes.recycle();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(templateType, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        nativeAdView = findViewById(R.id.native_ad_view);
        primaryView = findViewById(R.id.primary);
        secondaryView = findViewById(R.id.secondary);
        tertiaryView = findViewById(R.id.body);
        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setEnabled(false);
        callToActionView = findViewById(R.id.cta);
        iconView = findViewById(R.id.icon);
        mediaView = findViewById(R.id.media_view);
        background = findViewById(R.id.background);
    }

    public void updateTextColor(View child) {
        String b = new AppPreference(getContext()).getTextColor();
        if (child instanceof TextView) {
            ((TextView) child).setTextColor(Color.parseColor("#" + b));
        }
    }

    public void updateBg() {
        AppPreference preference = new AppPreference(getContext());
        String c = preference.getBackColor();
        if (!TextUtils.isEmpty(c)) {
            nativeAdView.getBackground().setColorFilter(Color.parseColor("#" + c), PorterDuff.Mode.SRC_ATOP);
        }

        String a = preference.getAdbtcolor();
        if (!TextUtils.isEmpty(a)) {
            callToActionView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + a)));
        }
    }

}
