#!/usr/bin/python

# Creates a file with each activity paired with each location batch.
# The file can be split based on the separator; each block is
# a valid MTurk question form.

from os import listdir
from os.path import isfile, join

sep = '=' * 72 + '\n'

def merge(infile, activities):
    questions = open('questions.question', 'a')
    locations = infile.readlines()
    locations = map(lambda x: x.strip(), locations)

    for activity in activities:
        questions.write(sep)
        questions.write('<?xml version="1.0" encoding="UTF-8"?>\n')
        questions.write('<QuestionForm xmlns="http://mechanicalturk.' +
                        'amazonaws.com/AWSMechanicalTurkDataSchemas/' +
                        '2005-10-01/QuestionForm.xsd">\n')
        questions.write('<Question>\n')
        questions.write('<QuestionIdentifier>' + activity.label +
                        '</QuestionIdentifier>\n')
        questions.write('<IsRequired>true</IsRequired>\n')
        questions.write('<QuestionContent>\n')
        questions.write('<Title>' + activity.label + '</Title>\n')
        questions.write('<Text>' + activity.description + '</Text>\n')
        questions.write('</QuestionContent>\n')
        questions.write('<AnswerSpecification>\n')
        questions.write('<SelectionAnswer>\n')
        questions.write('<StyleSuggestion>checkbox</StyleSuggestion>\n')
        questions.write('<Selections>\n')

        for i, location in enumerate(locations):
            questions.write('<Selection>\n')
            questions.write('<SelectionIdentifier>Q' + str(i) + 
                            '</SelectionIdentifier>\n')
            questions.write('<Text>' + location + '</Text>\n')
            questions.write('</Selection>\n')

        questions.write('</Selections>\n')
        questions.write('</SelectionAnswer>\n')
        questions.write('</AnswerSpecification>\n')
        questions.write('</Question>\n')
        questions.write('</QuestionForm>\n')

class Activity(object):
    def __init__(self, label, description):
        self.label = label
        self.description = description

    def __repr__(self):
        res = self.label + self.description
        return res

def create_questions():
    activities = []
    desc_file = open('label_description.txt', 'r').readlines()
    desc_file = map(lambda x: x.strip(), desc_file)
    for i in range(0, len(desc_file), 2):
        activities.append(Activity(desc_file[i], desc_file[i + 1]))

    batch_dir = 'batches/'
    batch_files = [f for f in listdir(batch_dir) 
                   if isfile(join(batch_dir, f))]

    for f in batch_files:
        merge(open(join(batch_dir, f), 'r'), activities)

create_questions()
