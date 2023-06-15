class Functions {

    fun <T> addToList(list: MutableList<T>, element: T): Int {
        list += element
        return list.lastIndex
    }


    fun <T> delete(list: MutableList<T>, id: Int): T? {
        for ((index, n) in list.withIndex()) {
            if (n is NotesServices.Notes && n.noteId == id) {
                list.removeAt(index)
                return n
            }
            if (n is NotesServices.Comments && n.commentId == id) {
                list.removeAt(index)
                return n
            }
        }
        return null
    }
}


class NotesServices {
    private var notes = mutableListOf<Notes>()
    private var comments = mutableListOf<Comments>()
    var notesDeleted = mutableListOf<Notes?>()
    var commentsDeleted = mutableListOf<Comments?>()

    private var noteId: Int = 1
    private var commentId: Int = 1

    data class Notes(
        val noteId: Int = 1,
        val userId: Int = 1,
        var title: String,
        var text: String,
        var privacy: Int = 3,
        var commentPrivacy: Int = 3
    )

    data class Comments(
        var noteId: Int,
        var commentId: Int = 1,
        var message: String
    )

    class NoteNotFoundException(message: String) : RuntimeException(message)
    class CommentNotFoundException(message: String) : RuntimeException(message)


    //                                            *** get  ***

    fun getNotes(): MutableList<Notes> {
        return notes
    }

    fun getAllComments(): MutableList<Comments> {
        return comments
    }


    //****

    fun getNoteById(noteId: Int): Notes {
        val result = notes.find { it.noteId == noteId }
        result ?: throw NoteNotFoundException("Note not found")
        return result
    }

    fun getCommentsToNote(noteId: Int): List<Comments> {
        val result = comments.filter { it.noteId == noteId }
        if (result.isEmpty()) {
            println("Нет комментариев к заметке $noteId или самой заметки не существует")
        }
        return result
    }


    //           ***  add generic  ***

    fun addNote(title: String, text: String): Int {
        return Functions().addToList(notes, Notes(noteId = noteId++, title = title, text = text))
    }

    fun addComment(noteId: Int, message: String): Int {
        for (n in notes) {
            if (noteId == n.noteId) {
                return Functions().addToList(
                    comments,
                    Comments(noteId = noteId, commentId = commentId++, message = message)
                )
            }
        }
        throw NoteNotFoundException("Note not found")
    }


    //                                                        *** delete generic ***

    fun deleteNote(noteId: Int): Int {
        val deletedNote = Functions().delete(notes, noteId)
        notesDeleted += deletedNote

        val commentsToNote = getCommentsToNote(noteId)
        if (commentsToNote.isNotEmpty()) {
            commentsDeleted.addAll(commentsToNote)
            comments.removeAll(commentsToNote)
        }

        deletedNote ?: throw NoteNotFoundException("Note not found")
        return 1
    }

    fun deleteComment(commentId: Int): Int {
        val deletedComment = Functions().delete(comments, commentId)
        commentsDeleted += deletedComment
        deletedComment ?: throw CommentNotFoundException("Comment not found")
        return 1
    }


    //                                                           *** edit ***
    fun editNote(noteId: Int, title: String, text: String): Int {
        for ((index, n) in notes.withIndex()) {
            if (noteId == n.noteId) {
                notes[index].title = title
                notes[index].text = text
                return 1
            }
        }
        throw NoteNotFoundException("Note not found")
    }


    fun editComment(commentId: Int, message: String): Int {
        for (c in comments) {
            if (commentId == c.commentId) {
                c.message = message
                return 1
            }
        }
        throw CommentNotFoundException("Comment not found")
    }


    //                                              *** Restore comment ***
    fun restoreComment(commentId: Int): Int {
        for (c in commentsDeleted) {
            if (commentId == c?.commentId) {
                comments += c.copy()
                comments.sortBy { it.noteId }
                return 1
            }
        }
        throw CommentNotFoundException("Comment not found")
    }
}

//********************************************************* MAIN **********************************************

fun main() {
//                                                         *** add ***
    println("ADD")
    val note = NotesServices()
    println(note.addNote("1", "note1"))
    println(note.addNote("2", "note2"))
    println(note.addNote("3", "note3"))
    println(note.addNote("4", "note4"))

    println(note.getNotes())

    println("ADD COMMENT")
    println(note.addComment(1, "comment 1"))
    println(note.addComment(2, "comment 2"))
    println(note.addComment(3, "comment 3"))
    println(note.addComment(3, "comment 4"))
    println(note.addComment(4, "comment 5"))

    println(note.getAllComments())

    println("Комментарии к заметке 3")
    println(note.getCommentsToNote(3))


//                                                  *** delete ***
    println("DELETE")
    println(note.deleteNote(2))
    println(note.getNotes())
    println("Удаленные заметки")
    println(note.notesDeleted)

    println("DELETE COMMENT")
    println(note.deleteComment(1))
    println(note.getAllComments())
//    println(note.deleteComment(2))
//    println(note.getAllComments())
    println("Комментарии к заметке...")
    println(note.getCommentsToNote(3))
    println(note.getCommentsToNote(2))
    println("Удаленные коммент:")
    println(note.commentsDeleted)

//                                                *** Restore comment ***
    println("Restore comment")
    println(note.restoreComment(1))
    println(note.getAllComments())
    println("Комментарии к заметке...")
    println(note.getCommentsToNote(1))


//                                                   *** edit ***
    println("EDIT")
    note.editNote(1, "1 CHANGED", "note1 CHANGED")
    println(note.getNotes())
    println(note.editComment(3, "CHANGED 1"))
    println(note.getAllComments())


//                                                     *** get by id ***

    println("GET NOTE BY ID")
    println(note.getNoteById(3))

}

