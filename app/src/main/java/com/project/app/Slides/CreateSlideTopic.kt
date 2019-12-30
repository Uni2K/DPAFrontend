package com.project.app.Slides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.app.Fragments.QuestionCreator
import com.project.app.Fragments.TopicPickFragment
import com.project.app.Fragments.TopicProviderFragment
import com.project.app.Interfaces.TopicControllerInterface
import com.project.app.Objects.Topic
import com.project.app.R

class CreateSlideTopic : Fragment() {
    lateinit var creatorParent: QuestionCreator

    fun setController(questionCreator: QuestionCreator){
        this.creatorParent=questionCreator
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_create_slide2, container, false)
        val tagFragment = TopicPickFragment()
        val bundle = Bundle()
        bundle.putInt("preset", TopicProviderFragment.PRESET_CREATE)
        bundle.putInt("topicLimit", 1)
        tagFragment.arguments = bundle

          tagFragment.setCallBack(object : TopicControllerInterface {
              override fun onSubTopicChosen(subtag: Topic) {
                  creatorParent.onChangeTopic(subtag)
              }

              override fun onSubTopicNotChosen(subtag: Topic) {
                  creatorParent.onChangeTopic(null)

              }

              override fun onClearAll() {
              }

              override fun onRestore() {
              }

              override fun onTopicsChanged() {
              }

          })



        childFragmentManager.beginTransaction().add(R.id.tagMaster, tagFragment, "tags").commit()

        return v
    }
}