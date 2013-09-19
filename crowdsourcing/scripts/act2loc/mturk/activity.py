class Activity(object):
    def __init__(self, label, description, category):
        self.label = label
        self.description = description
        self.category = category

    def __repr__(self):
        res = self.label + self.description

        return res
