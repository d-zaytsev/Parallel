# Analysis of the project [Threaded-Spell-Checker](https://github.com/shaansaharan170/Threaded-Spell-Checker)
In this task, it was necessary, using special tools Helgrind and ThreadSanitizer, to analyze someone else’s project.
## Briefly about the project
The project is a student's parallel programming homework. It is implemented in the C programming language, uses POSIX Threads. Allows to analyze spelling of several text files at the same time. The project also contains makefile *(upgraded by me)*, with which you can quickly build and test the project by yourself. 
## Project Analysis
The program uses a dictionary file provided by the user to check words spelling. It stores it into hash table and then compares the words of the file with the correct words in that data structure. Program uses mutexes to update global variables and save the results to a output file. Parallelism in this program is needed to process a large number of files simultaneously in different threads using different dictionaries. 
## First check
After successfully building the project using Helgrind and ThreadSanitizer, it was tested on medium-sized text (created by chat-gpt). No errors were found in the work.
## Try to break everything
Firstly, it is necessary to make an artificial delay when processing files. This way we can track errors more effectively!
```C
#include <unistd.h>
// <...>
if (!findWord(wordFound))
{
    // If word is not found in the dictionary
    sleep(1); // <<-- Wait for 1 second  
    outputResult->totalMisspelledWords++; // Increment count of misspelled words
    updateCount(outputResult, wordFound); // Update count for this specific misspelled word
}
```
Then I start checking two files at once (to create several threads) -> Helgrind warns: *"Possible data race during read..."*. 

Judging by the functions indicated by Helgrind, an error was made in the functions accessing the dictionary. Most likely, the warning occurs because we have the opportunity to process files by specifying different dictionaries. At the same time, program can contain only 1 dictionary => while thread 1 is checking information from the dictionary structure, thread 2 can overwrite the data in it. 

I decided to test my theory and started processing the same file twice, but with two different dictionaries: a broken one (which prevented normal checking) and a correct one. As a result, I got zero errors because the second (correct) dictionary overwrote the first (incorrect) dictionary, which just could not happen if executed correctly. **We got a data race**: the result depends on the sequence of data (if I had submitted the wrong dictionary second, everything would have been in errors).
```
****************** Final Summary ******************
Number of files processed: 2
Number of spelling errors: 0
***************************************************
```
This can be easily **fixed** by preventing the user from using different dictionaries for different files (which would be logical).

Further testing and analysis of the code failed to identify other problems, since the program used mutexes quite successfully. However, I had a desire to see how Helgrind would behave if I caused a deadlock. I decided to do this in the standard way: lock one mutex and don't unlock it (instead unlock the another). Theoretically, during the development of this program, such situations could happen, because the names of mutexes are very similar:
```C
pthread_mutex_t mutexInputFile;  // Mutex for synchronizing access to input files
pthread_mutex_t mutexOutputFile; // Mutex for synchronizing access to output files
```
That's what I did:
```C
// Logs the results of spell checking to a file or standard output
void logToFile(const char *inFile, SpellCheckTask *outputResult)
{
    pthread_mutex_lock(&mutexInputFile); // Lock the mutex to ensure thread-safe access to the output file
    // ...<code>...
    pthread_mutex_unlock(&mutexOutputFile); // Unlock the mutex (another)
}
```
As a result, thread 2 cannot execute the function because the mutex is locked => the program is stuck. That's what Helgrind say to us:
*"Thread #2 unlocked a not-locked lock at 0x10D0C0"*. As expected, the tool quickly found out the cause of the deadlock!
## Results
After analyzing this program using special tools, we were able to find non-obvious errors. It was also possible to create a dangerous situation and analyze it using the same tools.
