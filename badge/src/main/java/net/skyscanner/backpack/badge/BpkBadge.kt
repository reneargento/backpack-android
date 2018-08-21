package net.skyscanner.backpack.badge

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout


open class BpkBadge(
  context: Context,
  attrs: AttributeSet?,
    defStyleAttr: Int) : AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.style.Bpk_badge)

    init {
      initialize(context, attrs, defStyleAttr)
      setup()
    }

    /**
     * @property type
     * Type of badge. Default Type.Success
     */
    var type: Type = Type.Success
      set(value) {
        field = value
        setup()
      }
    /**
     * @property message
     * message on the badge
     */
    var message: String? = null
      set(value) {
        field = value
        this.text = message
      }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

      val a: TypedArray = context.theme.obtainStyledAttributes(
        attrs,
        R.styleable.badge,
        0, 0)

      if (a.hasValue(R.styleable.badge_type)) {
        type = Type.fromId(a.getInt(R.styleable.badge_type, 0))
      }
      if (a.hasValue(R.styleable.badge_message)) {
        message = a.getString(R.styleable.badge_message)
      }

      a.recycle()
    }

    private fun setup() {
      if (message != null) {
        this.text = message
      }

      if (type == null) {
        return
      }
      //set padding
      val paddingMd = resources.getDimension(R.dimen.bpkSpacingMd).toInt()
      val paddingSm = resources.getDimension(R.dimen.bpkSpacingSm).toInt()
      this.setPadding(paddingMd, paddingSm, paddingMd, paddingSm)

      //set Text color
      this.setTextColor(context.resources.getColor(type.textColor))

      // Set background color
      val border = GradientDrawable()
      border.setColor(context.resources.getColor(type.bgColor))

      //Set border
      if (type == Type.Outline) {
        border.setStroke(resources.getDimension(R.dimen.badge_border_size).toInt(), context.resources.getColor(R.color.bpkWhite))
        //set alpha for border
        border.setColor(context.resources.getColor(type.bgColor) and 0x32ffffff)
      }

      //set corner radius
      val cornerRadius = context.resources.getDimension(R.dimen.bpkBorderRadiusSm)

      val radius = floatArrayOf(cornerRadius, cornerRadius,
        cornerRadius, cornerRadius,
        cornerRadius, cornerRadius,
        cornerRadius, cornerRadius)
      border.cornerRadii = radius
      this.background = border

      val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
      this.gravity = Gravity.CENTER
      this.layoutParams = params
    }

  enum class Type constructor(internal var id: Int,
                              internal var type: String,
                              internal var bgColor: Int,
                              internal var textColor: Int) {
    /**
     * Style for badges with positive messages
     */
    Success(1, "success", R.color.bpkGreen500, R.color.bpkGray700),
    /**
     *  Style for badges with warning messages
     */
    Warning(2, "warning", R.color.bpkYellow500, R.color.bpkGray700),
    /**
     * Style for badges with error messages
     */
    Destructive(3, "destructive", R.color.bpkRed500, R.color.bpkWhite),
    /**
     *  Light themed style for badges
     */
    Light(4, "light", R.color.bpkGray50, R.color.bpkGray700),
    /**
     *  Style for badges on dark themes
     */
    Inverse(5, "inverse", R.color.bpkWhite, R.color.bpkGray700),
    /**
     * Style for badges with a thin white outline
     */
    Outline(6, "outline", R.color.bpkWhite, R.color.bpkWhite);

    internal companion object {

      internal fun fromId(id: Int): Type {
        for (f in values()) {
          if (f.id == id) return f
        }
        throw IllegalArgumentException()
      }
    }
  }
}