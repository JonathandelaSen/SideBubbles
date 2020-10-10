package devjdelasen.com.sidebubbles

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.side_bubbles_item.view.*


internal class SideBubblesItem : RelativeLayout {

    enum class Types(val value: Int) {
        SAM_DEFAULT(0),
    }

    private var type: Int = Types.SAM_DEFAULT.value




    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SideBubblesItem, 0, 0)
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
        View.inflate(context, R.layout.side_bubbles_item, this)
        setView()
    }


    fun set(icIcon: Int, bgColor: Int) {
        sb_ivIcon.setImageDrawable(ContextCompat.getDrawable(context, icIcon))
        //flRoot.backgroundTintList = ContextCompat.getColorStateList(context, bgColor)
        sb_flRoot.background.setTint(bgColor)
    }


    private fun setView() {
        context?.let {
            resources?.let { resources ->
                when(type) {
                    Types.SAM_DEFAULT.value -> {
                        setDefaultView()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setDefaultView() {

    }


}
