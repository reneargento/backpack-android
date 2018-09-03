package net.skyscanner.backpack.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView

import net.skyscanner.backpack.demo.data.ComponentRegistry
import android.widget.ArrayAdapter



/**
 * An activity representing a list of Components. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ComponentDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    private var mTheme: Int = R.style.AppTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_component_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title


        if (findViewById<View>(R.id.component_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }
        val recyclerView = findViewById<View>(R.id.component_list)!!
        setupRecyclerView(recyclerView as RecyclerView)

        val themesSpinner = findViewById<Spinner>(R.id.themesSpinner)
        val adapter = ArrayAdapter.createFromResource(
          this,
          R.array.themes_array,
          android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themesSpinner.adapter = adapter
        themesSpinner.onItemSelectedListener = this
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, ComponentRegistry.ITEMS, mTwoPane)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {
      val selection = parent.getItemAtPosition(pos) as String
      mTheme = if (selection == "LondonTheme") {
        R.style.LondonTheme
      } else {
        R.style.AppTheme
      }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
      mTheme = R.style.AppTheme
    }

    private class SimpleItemRecyclerViewAdapter internal constructor(private val mParentActivity: MainActivity,
                                                                     private val mValues: List<ComponentRegistry.Component>,
                                                                     private val mTwoPane: Boolean) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private val mOnClickListener = View.OnClickListener { view ->
            val item = view.tag as ComponentRegistry.Component
            if (mTwoPane) {
                val arguments = Bundle()
                arguments.putString(ComponentDetailFragment.ARG_ITEM_ID, item.id)
                arguments.putInt(ComponentDetailFragment.THEME, mParentActivity.mTheme)
              var fragment: ComponentDetailFragment?
              try {
                    fragment = item.fragmentClass.newInstance()
                } catch (e: InstantiationException) {
                    throw RuntimeException(e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                }

                fragment!!.arguments = arguments
                mParentActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.component_detail_container, fragment)
                        .commit()
            } else {
                val context = view.context
                val intent = Intent(context, ComponentDetailActivity::class.java)
                intent.putExtra(ComponentDetailFragment.ARG_ITEM_ID, item.id)
                intent.putExtra(ComponentDetailFragment.THEME, mParentActivity.mTheme)

                context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.component_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = mValues[position].id

            holder.itemView.tag = mValues[position]
            holder.itemView.setOnClickListener(mOnClickListener)
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView

            init {
                name = view.findViewById(R.id.component_name)
            }
        }
    }
}
