if ARGV.size == 2
  theirExec = ARGV.first
  myExec = ARGV.last
else
  puts "Expected command: tests.rb THEIREXECUTABLE YOUREXECUTABLE"
  exit
end

words = []
File.readlines('words').each do |line|
  words << line
end
words = words.select{|word| word.length > 5}

tests = []
letters = []
26.times { |i| letters << (i + 97).chr }
letters += letters
random = 0
command = ""
mostCommon = "etaoinshr"
sample = "word"
100.times do
  random = (rand*200).to_i
  command = mostCommon.split("").shuffle.join("\n") + letters.shuffle.join("\n")
  tests << system("echo \"#{ command }\" | ./#{theirExec} words #{random} > theiroutput.txt && echo \"#{ command }\" | ./#{myExec} words #{random} > myoutput.txt && diff -y theiroutput.txt myoutput.txt")
  break unless tests.last
  puts "\n\n"
end
10.times do
  random = (rand*200).to_i
  sample = words.sample
  tests << system("echo \"#{ sample + "\n + n\n"}\" | ./#{theirExec} words #{random} > theiroutput.txt && echo \"#{ words.sample }\" | ./#{myExec} words #{random} > myoutput.txt && diff -y theiroutput.txt myoutput.txt")
  unless tests.last
    puts "random: #{random} word: #{sample}"
    break
  end
end

word = "nuzzling".split("").join("\n")
tests << system("echo \"#{ word }\" | ./#{theirExec} words 20 > theiroutput.txt && echo \"#{ word }\" | ./#{myExec} words 20 > myoutput.txt && diff -y theiroutput.txt myoutput.txt")
word = "nuZzling".split("").join("\n")
tests << system("echo \"#{ word }\" | ./#{theirExec} words 20 > theiroutput.txt && echo \"#{ word }\" | ./#{myExec} words 20 > myoutput.txt && diff -y theiroutput.txt myoutput.txt")
tests << system("echo \"nuzzling\" | ./#{theirExec} words 20 > theiroutput.txt && echo \"nuzzling\" | ./#{myExec} words 20 > myoutput.txt && diff -y theiroutput.txt myoutput.txt")
tests << system("echo \"Nuzzling\" | ./#{theirExec} words 20 > theiroutput.txt && echo \"Nuzzling\" | ./#{myExec} words 20 > myoutput.txt && diff -y theiroutput.txt myoutput.txt")


if tests.all?
  puts "\nAll passed"
else
  puts "\nDid not pass"
end
