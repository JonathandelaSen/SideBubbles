package devjdelasen.com.sidebubbles

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.side_bubbles_button.view.*


internal class SideBubblesButton : ConstraintLayout {

    enum class Types(val value: Int) {
        SAMB_DEFAULT(0)
    }

    private var type: Int = Types.SAMB_DEFAULT.value




    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SideBubblesButton, 0, 0)
        try {
            //type = ta.getInt(R.styleable.ChatAvatar_chat_avatar_type, Types.CHATS_LIST_ITEM.value)
        } finally {
            ta.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.side_bubbles_button, this)
    }

    fun set(icColor: Int, bgColor: Int) {
        ImageViewCompat.setImageTintList(sb_ivIcon, ColorStateList.valueOf(icColor));
        //sb_vCircle.backgroundTintList = ContextCompat.getColorStateList(context, bgColor)
        sb_vCircle.background.setTint(bgColor)


        //vBase.backgroundTintList = ContextCompat.getColorStateList(context, bgColor)
        sb_vBase.background.setTint(bgColor)

    }




}
