package com.thf.journal;

import androidx.room.Embedded;
import androidx.room.Relation;

public class JournalEntryWithTag {

    @Embedded
    public JournalEntry journalEntry;

    @Relation(
            parentColumn = "tag_id",
            entityColumn = "id"
    )
    public JournalEntryTag tag;

    public JournalEntryWithTag(JournalEntry journalEntry, JournalEntryTag tag){
        this.journalEntry = journalEntry;
        this.tag = tag;
    }
}
