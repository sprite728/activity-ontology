require_relative "login"

# Modify Hit class to allow iterating over more than 100 results
module RTurk
  class Hit
    def self.each(page_number=1, &block)
      results = RTurk::SearchHITs.create(page_number:page_number, page_size:100)
      num_results = results.xpath("//TotalNumResults").text.to_i

      results.hits.map do |hit|
        yield new(hit.id, hit)
      end
      each(page_number + 1, &block) if num_results > page_number * 100
    end
  end
end
