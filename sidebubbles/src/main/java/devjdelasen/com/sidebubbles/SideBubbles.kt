package devjdelasen.com.sidebubbles

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.*
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.side_bubbles.view.*


class SideBubbles : RelativeLayout {


    private val TIME_MENU_OPENED: Long = 1800
    private val START_ANIMATION_TIME_OFFSET_INCREMENT_START_ITEM_ANIM = 50
    private val START_ANIMATION_TIME_OFFSET_DECREMENT_FINISH_ITEM_ANIM = 200
    private val START_ANIMATION_TIME_BASE_FINISH_ITEM_ANIM = 2000
    private val CLOSE_ANIMATION_TIME_OFFSET_INCREMENT_FINISH_ITEM_ANIM = 200
    private val CLOSE_ANIMATION_TIME_BASE_FINISH_ITEM_ANIM = 20
    private val SELECTED_ANIMATION_TIME_OFFSET_ITEM_ANIM: Long = 10


    enum class Types(val value: Int) {
        SB_DEFAULT(0),
    }


    private var type: Int = Types.SB_DEFAULT.value
    private var items: ArrayList<SideBubblesItem> = ArrayList()
    private var handlerLongClick: Handler? = null
    private var runnableRunnerStartFadeOut: Runnable? = null
    private var isOpenAnimationOn = false
    private var isClosingAnimation = false
    private var defaultBgColor: Int = R.color.colorAccent
    private var defaultIconColor: Int = R.color.white
    private var listener: OnClickItemListener? = null




    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SideBubbles, 0, 0)
        try {
            defaultBgColor = ta.getInt(R.styleable.SideBubbles_side_bubbles_menu_color, R.color.colorAccent)
            defaultIconColor = ta.getInt(R.styleable.SideBubbles_side_bubbles_menu_tint_icons_color, R.color.white)
        } finally {
            ta.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.side_bubbles, this)
        setView()
        setListeners()
    }

    fun setClickItemListener(listener: OnClickItemListener) {
        this.listener = listener
    }

    fun addItem(id: String, icIcon: Int) {
        addItem(id, icIcon, defaultIconColor, defaultBgColor)
    }

    fun addItem(id: String, icIcon: Int, icColor: Int, bgColor: Int) {
        val item = SideBubblesItem(context)
        item.set(icIcon, icColor, bgColor)
        item.tag = id
        item.visibility = View.GONE

        val itemId = View.generateViewId()
        item.id = itemId
        clRoot.addView(item, clRoot.childCount-1)
        items.add(item)
        getConstraints(itemId, if (items.size == 1) sbb.id else items[items.size-2].id).applyTo(clRoot)
        addListener(item)
    }


    private fun addListener(item: SideBubblesItem) {
        item.setOnClickListener {
            setSelectedAnimation(it, SELECTED_ANIMATION_TIME_OFFSET_ITEM_ANIM, object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    startCloseAnimations()
                }

                override fun onAnimationStart(p0: Animation?) {
                }

            })
            listener?.onClickItem(item.tag as String)
        }
    }

    private fun setView() {
        context?.let {
            resources?.let { resources ->
                when(type) {
                    Types.SB_DEFAULT.value -> {
                        setDefaultView()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setDefaultView() {
        sbb.set(defaultIconColor, defaultBgColor)
    }

    private fun getConstraints(id: Int, idToBeAbove: Int): ConstraintSet {
        val set = ConstraintSet()
        set.clone(clRoot)
        set.connect(
            id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            0
        )

        set.connect(
            id,
            ConstraintSet.BOTTOM,
            idToBeAbove,
            ConstraintSet.TOP,
            0
        )
        return set
    }

    private fun setListeners() {
        setOpenMenuTouchListener()
    }

    private fun setOpenMenuTouchListener() {
        clRoot.setOnTouchListener(object : View.OnTouchListener {

            var mLongPressed = Runnable {
                isLongPress = true
                Log.i("HnbActivity onTouch", "Runnable Long press!")
                starOpenAnimations()
            }
            var isLongPress = false


            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handlerLongClick?.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout().toLong()/2);
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        handlerLongClick?.removeCallbacks(mLongPressed);
                        if (isLongPress) {
                            Log.i("HnbActivity onTouch", "Long press!")
                            findSelected(view as ConstraintLayout, event)
                        }
                        else {
                            Log.i("HnbActivity onTouch", "Regular press!")
                            handleOpenCloseMenu()
                        }
                        isLongPress = false
                        return true
                    }
                }
                return false
            }

        })

    }


    private fun starOpenAnimations() {
        activeMenuAnimation()
        isOpenAnimationOn = true

        items.forEachIndexed { index, item ->
            item.visibility = View.VISIBLE
            val animation = AnimationSet(false) //change to false
            animation.addAnimation(scaleAnimation((START_ANIMATION_TIME_OFFSET_INCREMENT_START_ITEM_ANIM * index).toLong()))
            animation.addAnimation(getFadeIn((START_ANIMATION_TIME_OFFSET_INCREMENT_START_ITEM_ANIM * index).toLong()))
            animation.addAnimation(getFadeOut(getOffsideTimeItemStartAnimationFinishFadeOff(index), object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    item.visibility = View.INVISIBLE
                    if (index == 0) {
                        isOpenAnimationOn = false
                        isClosingAnimation = false
                        Log.i("HnbActivity", "starOpenAmimations getFadeOut onAnimationEnd isClosingAnimation = false")
                        deactivateMenuAnimation()
                        return
                    }
                }

                override fun onAnimationStart(p0: Animation?) {
                    if (index == items.size-1) {
                        handlerLongClick = Handler()
                        runnableRunnerStartFadeOut = Runnable {
                            isClosingAnimation = true
                            Log.i("HnbActivity", "starOpenAmimations getFadeOut onAnimationStart isClosingAnimation = true")
                        }
                        handler?.postDelayed(runnableRunnerStartFadeOut, TIME_MENU_OPENED)
                    }
                }

            }))
            item.startAnimation(animation)
        }
    }

    private fun startCloseAnimations() {
        Log.i("HnbActivity", "startCloseAnimations isClosingAnimation = true")
        isOpenAnimationOn = false
        isClosingAnimation = true
        runnableRunnerStartFadeOut?.let {
            handler?.removeCallbacks(it)
        }

        items.forEachIndexed { index, item ->
            item.clearAnimation()

            val animation = AnimationSet(false)
            animation.addAnimation(getFadeOut(getOffsideTimeItemCloseAnimationFinishFadeOff(index), object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    item.visibility = View.INVISIBLE
                    if (index == 0) {
                        Log.i("HnbActivity", "startCloseAnimations onAnimationEnd isClosingAnimation = false")
                        isClosingAnimation = false
                        deactivateMenuAnimation()
                    }
                }

                override fun onAnimationStart(p0: Animation?) {
                }

            }))
            item.startAnimation(animation)
        }
    }

    private fun getOffsideTimeItemStartAnimationFinishFadeOff(index: Int): Long {
        return (START_ANIMATION_TIME_BASE_FINISH_ITEM_ANIM + ( (items.size-1) * START_ANIMATION_TIME_OFFSET_DECREMENT_FINISH_ITEM_ANIM) -  (index * START_ANIMATION_TIME_OFFSET_DECREMENT_FINISH_ITEM_ANIM)).toLong()
    }

    private fun getOffsideTimeItemCloseAnimationFinishFadeOff(index: Int): Long {
        return (CLOSE_ANIMATION_TIME_BASE_FINISH_ITEM_ANIM + ( (items.size-1) * CLOSE_ANIMATION_TIME_OFFSET_INCREMENT_FINISH_ITEM_ANIM) -  (index * CLOSE_ANIMATION_TIME_OFFSET_INCREMENT_FINISH_ITEM_ANIM)).toLong()
    }

    private fun getFadeIn(startOffset: Long = 0): AlphaAnimation {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = AccelerateInterpolator() //add this
        fadeIn.startOffset = startOffset
        fadeIn.duration = 400
        return fadeIn
    }

    private fun getFadeOut(startOffset: Long = 0, animationListener: Animation.AnimationListener): AlphaAnimation {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.startOffset = startOffset
        fadeOut.duration = 300
        fadeOut.isFillEnabled = true
        fadeOut.fillAfter = true
        fadeOut.setAnimationListener(animationListener)
        return fadeOut
    }

    private fun scaleAnimation(startOffset: Long = 0): Animation? {
        val anim: Animation = ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.fillAfter = true
        anim.duration = 200
        anim.startOffset = startOffset
        return anim
    }

    private fun activeMenuAnimation() {
        sbb.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(100)
            .translationX(0f)
            .withLayer()
    }

    private fun deactivateMenuAnimation() {
        sbb.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(100)
            .translationX(convertToPx(20).toFloat())
            .withLayer()
    }

    private fun handleOpenCloseMenu() {
        if (isOpenAnimationOn) {
            if (!isClosingAnimation) {
                startCloseAnimations()
            }
            return
        }
        starOpenAnimations()
    }

    private fun setSelectedAnimation(view: View, startOffset: Long = 0, animationListener: Animation.AnimationListener) {
        val anim = AlphaAnimation(1f, 0f)
        anim.interpolator = MyBounceInterpolator(6.0, 1.0) //and this
        anim.startOffset = startOffset
        anim.duration = 50
        anim.setAnimationListener(animationListener)
        view.startAnimation(anim)
    }

    private fun findSelected(view: ConstraintLayout, event: MotionEvent?) {
        Log.i("HnbActivity onTouch", "onTouch findSelected layout.childCount ${view.childCount}")
        Log.i("HnbActivity onTouch", "onTouch findSelected event x ${event?.x}, y ${event?.y}")
        for (i in 0 until view.childCount) {
            val viewChild = view.getChildAt(i)
            if (viewChild is SideBubblesItem) {
                Log.i("HnbActivity onTouch", "is FrameLayout tag ${viewChild.tag}")
                val outRect = Rect(viewChild.left, viewChild.top, viewChild.right, viewChild.bottom)
                if (outRect.contains(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)) {
                    Log.i("HnbActivity onTouch", "is FrameLayout Touched tag ${viewChild.tag}")

                    listener?.onClickItem(viewChild.tag as String)
                    setSelectedAnimation(viewChild, 10, object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            startCloseAnimations()
                        }

                        override fun onAnimationStart(p0: Animation?) {
                        }
                    })
                }
            }
        }
    }


    private fun convertToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private class MyBounceInterpolator(amplitude: Double, frequency: Double) :
        Interpolator {
        private var mAmplitude = 0.0
        private var mFrequency = 0.0
        override fun getInterpolation(time: Float): Float {
            return (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1).toFloat()
        }

        init {
            mAmplitude = amplitude
            mFrequency = frequency
        }
    }


    interface OnClickItemListener {
        fun onClickItem(id: String)
    }
}
