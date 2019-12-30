package com.project.app.Objects

class QuestionSnapshot {

    var timestamp:String="-1"
    var questionID:String="-1"
    var votes = Array<Int>(4) { 0 }

    constructor(timestamp: String, questionID: String, votes: Array<Int>) {
        this.timestamp = timestamp
        this.questionID = questionID
        this.votes = votes
    }
}