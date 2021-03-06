package com.jayboat.reddo.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Todo
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.item_check_text.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent
import kotlin.math.absoluteValue

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/25 16:40
 * Description: 可点击/滑动的TodoItemView，仅调用refreshView
 */
class TodoItemView constructor(
        context: Context
) : RelativeLayout(context) {

    private lateinit var todo: Todo
    private lateinit var vm: EntryViewModel

    init {
        LayoutInflater.from(context).inflate(R.layout.item_check_text, this, true).apply {
            layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
        }
    }

    private var point = 0f to 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (todo.isDone) {
            return super.onTouchEvent(e)
        }
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                point = e.x to e.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (point.first < 0) {
                    return false
                }
                val d = (point.first - e.x).absoluteValue to (point.second - e.y).absoluteValue
                if (d.first > d.second) {
                    todo.isActivate = !todo.isActivate
                    if (todo.isActivate) {
                        checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_normal)
                        ObjectAnimator.ofFloat(line, "translationX", 0f, this.width.toFloat()).apply {
                            duration = 100
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    line.visibility = View.GONE
                                    vm.updateTodo(todo)
                                }
                            })
                        }.start()
                    } else {
                        checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_cancel)
                        line.backgroundColor = Color.parseColor("#E10000")
                        line.visibility = View.VISIBLE
                        ObjectAnimator.ofFloat(line, "translationX", this.width.toFloat(), 0f).apply {
                            duration = 100
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    vm.updateTodo(todo)
                                }
                            })
                        }.start()
                    }
                    point = -1f to -1f
                    return false
                } else if (d.first > 100 || d.second > 100) {
                    point = d
                }
            }
        }
        return super.onTouchEvent(e)
    }

    fun refreshView(t: Todo, m: EntryViewModel = vm, editable: Boolean = false) {
        this.todo = t
        this.vm = m
        this.apply {

            if (!todo.isActivate) {
                checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_cancel)
                line.visibility = View.VISIBLE
                line.backgroundColor = Color.parseColor("#E10000")
                tv_todo.isEnabled = false
            } else if (todo.isDone) {
                checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_checked)
                line.visibility = View.VISIBLE
                line.backgroundColor = Color.parseColor("#3FA55C")
                tv_todo.isEnabled = false
            } else {
                checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_normal)
                line.visibility = View.GONE
                tv_todo.isEnabled = editable
            }

            tv_todo.setText(todo.describe)
            if (editable) {
                tv_todo.setOnEditorActionListener { v, actionId, event ->
                    val r = tv_todo.text.toString()
                    val str = r.replace(Regex("\n"), "")
                    if (r.length != str.length) {
                        tv_todo.setText(str)
                    } else
                        if (todo.describe != str) {
                            todo.describe = str
                            vm.updateTodo(todo)
                        }
                    false
                }
            } else {
                tv_todo.background = null
            }

            checkbox_todo.setOnClickListener {
                if (!todo.isActivate) {
                    return@setOnClickListener
                }
                todo.isDone = !todo.isDone
                if (todo.isDone) {
                    checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_checked)
                    line.backgroundColor = Color.parseColor("#3FA55C")
                    line.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(line, "translationX", this.width.toFloat(), 0f).apply {
                        duration = 100
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                vm.updateTodo(todo)
                            }
                        })
                    }.start()
                } else {
                    checkbox_todo.setBackgroundResource(R.drawable.ic_checkbox_normal)
                    ObjectAnimator.ofFloat(line, "translationX", 0f, this.width.toFloat()).apply {
                        duration = 100
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                line.visibility = View.GONE
                                vm.updateTodo(todo)
                            }
                        })
                    }.start()
                }
            }
        }
    }
}