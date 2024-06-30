package com.aitgacem.openmal.ui.fragments.details

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentViewImagesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.transition.MaterialSharedAxis
import com.ortiz.touchview.TouchImageView

class ViewImagesFragment : Fragment() {
    private lateinit var binding: FragmentViewImagesBinding
    private val args by navArgs<ViewImagesFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val pager = binding.pager
        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val progress = String.format(
                    getString(R.string.progress_format),
                    (position + 1).toString(), args.urlList.size.toString()
                )
                binding.progressPics.text = progress
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Hack to reset the zoom state after swiping away from a picture
                if (state == SCROLL_STATE_IDLE) {
                    val recyclerView = pager.children.elementAt(0) as? RecyclerView
                    recyclerView?.children?.forEach { view ->
                        val linearLayout = view as? LinearLayout
                        val touchImageView = linearLayout?.getChildAt(0) as? TouchImageView
                        touchImageView?.resetZoom()
                    }
                }
            }
        })

        val adapter = ImageViewAdapter()
        pager.adapter = adapter
        adapter.list.addAll(args.urlList)
        adapter.notifyItemRangeInserted(0, args.urlList.size)
    }
}

class ImageViewAdapter :
    Adapter<ImageViewAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ClickableViewAccessibility")
        val image: TouchImageView = itemView.findViewById<TouchImageView?>(R.id.image).apply {
            setOnTouchListener { view, event ->
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if ((event.pointerCount >= 2) || (view.canScrollHorizontally(1) && canScrollHorizontally(
                        -1
                    ))
                ) {
                    // Disable the default swiping when the image is zoomed-in
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                }
                true
            }
        }

    }

    val list: MutableList<String> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val linearLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.image_view_content, parent, false)
        return ImageViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val context = holder.itemView.context
        val url = list[position]
        val imageView = holder.image
        Glide.with(context).load(url)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit

            })
    }

    override fun getItemCount(): Int {
        return list.size
    }

}


