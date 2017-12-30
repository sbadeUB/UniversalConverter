# UniversalConverter
Package that contains utilities to convert between different units with basic in-memory caching feature.
#### Converter-1: CurrencyConverter
##### Step-1: Converter gets inputs interactively from command line.
##### Step-2: Then checks if the currency conversion rate is available for input currency to output currency type from in-memory cache.
##### Step-3: If not available from cache, gets the latest exchange rate from "http://fixer.io/" end-point and stores the 2-way currency mapping in cache.
##### Step-4: Finally, the output currency value is computed using the above retrived conversion rate and prints it out to console.

Pretty straight-forward impl, with future scope to improve caching functionality and adding more converters related to banking sector.
