# SideBubbles

[![](https://jitpack.io/v/JonathandelaSen/SideBubbles.svg)](https://jitpack.io/#JonathandelaSen/SideBubbles)


Android library to provide a sticky side menu with options in form of bubbles.

![Video usage](/Screenshots/SideBubbles%20real%20use.gif?raw=true)



## Instalation 🛠

### Gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
 
Add the dependency

	dependencies {
	        implementation 'com.github.JonathandelaSen:SideBubbles:0.0.1'
	}
  
  ### Maven
  
  	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>


	<dependency>
	    <groupId>com.github.JonathandelaSen</groupId>
	    <artifactId>SideBubbles</artifactId>
	    <version>Tag</version>
	</dependency>
	

## Usage 📲


Set the layout

```XML
<devjdelasen.com.sidebubbles.SideBubbles
        android:id="@+id/sideBubbles"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginBottom="36dp"
        app:side_bubbles_bg_color="@color/colorAccent"
        app:side_bubbles_menu_color="@color/white"
        />
```


Set the bubbles

```kotlin
sideBubbles.addItem("chat", R.drawable.ic_chat, ContextCompat.getColor(this, R.color.colorAccent))
sideBubbles.addItem("geo", R.drawable.ic_geo, ContextCompat.getColor(this, R.color.colorAccent))
sideBubbles.addItem("ice_cream", R.drawable.ic_ice_cream)
sideBubbles.setClickItemListener(object: SideBubbles.OnClickItemListener {
    override fun onClickItem(id: String) {
        Toast.makeText(this@MainActivity, id, Toast.LENGTH_SHORT).show()
    }
})
        
```


#### Enjoy 🎉



## Features ✅

- [x] Click to open
- [x] Long click and slide to select


## Next 🚀
  
- [ ] Different animations
- [ ] Left side support



## Author
👨🏻‍💻  [Jonnie](https://www.instagram.com/devjdelasen/)
