package devjdelasen.com.sidebubbleslib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import devjdelasen.com.sidebubbles.SideBubbles
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sideBubbles.addItem("chat", R.drawable.ic_chat)
        sideBubbles.addItem("geo", R.drawable.ic_geo)
        sideBubbles.addItem("ice_cream", R.drawable.ic_ice_cream)
        sideBubbles.setClickItemListener(object: SideBubbles.OnClickItemListener {
            override fun onClickItem(id: String) {
                Toast.makeText(this@MainActivity, id, Toast.LENGTH_SHORT).show()
            }
        })
    }
}