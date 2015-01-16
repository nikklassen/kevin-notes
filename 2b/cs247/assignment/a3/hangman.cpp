#include <algorithm>
#include <fstream>
#include <iostream>
#include <iterator>
#include <stdlib.h>
#include <string>
#include <vector>


namespace {
    bool is_alpha_string(std::string test) {
        return test.find_first_not_of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz") == std::string::npos;
    }

    bool is_unacceptable(std::string test) {
        return test.length() < 6 || !is_alpha_string(test);
    }

    void print_item(char c) {
        std::cout << ' ' << c;
    }

    class replacer {
        const char c;
    public:
        replacer(char c) : c(c) { }
        char operator()(char lhs, char rhs) {
            if(lhs == c) {
                return lhs;
            } else {
                return rhs;
            }
        }
    };
}


class Hangman {
    std::vector<std::string> wordlist;

    std::string word;
    std::string view;
    std::vector<char> guesses;
    std::string guess;
    int lives;

    std::string temp;

    bool end_game(bool, bool = false);
    void print_state();
public:
    Hangman(std::string);

    void output_wordlist();
    void play_round();
};


// construct wordlist, filter unacceptable words
Hangman::Hangman(std::string filename) {
    std::ifstream infile(filename.c_str(), std::ios::in);
    if(!infile.is_open()) {
        std::cout << "Error: Could not open file \"" << filename << "\"." << std::endl;
        exit(-1);
    }

    std::istream_iterator<std::string> infile_iterator(infile);
    std::remove_copy_if(infile_iterator, std::istream_iterator<std::string>(), std::back_inserter(wordlist), is_unacceptable);
    if(wordlist.size() == 0) {
        std::cout << "Error: Pool of game words is empty." << std::endl;
        exit(-1);
    }
}


// print end game message, ask to play again
bool Hangman::end_game(bool victory, bool silence /* = false */) {
    std::cout << "You ";
    if(victory) {
        std::cout << "WIN!";
    } else {
        std::cout << "LOSE!";
    }
    if(!silence) {
        std::cout << "  The word was \"" << word << "\".";
    }
    std::cout << std::endl;
    std::cout << "Do you want to play again? [Y/N] ";
    if(!getline(std::cin, temp)) {
        std::cin.clear();
        exit(-1);
    }
    return temp.find("y") == 0 || temp.find("Y") == 0;
}

// play one round of hangman
void Hangman::play_round() {
    word = wordlist.at(lrand48() % wordlist.size());
    view = std::string(word.length(), '-');
    guesses.clear();
    lives = 5;

    while(true) {
        print_state();

        std::cout << "Next guess: ";
        if(!getline(std::cin, guess)) {
            std::cin.clear();
            exit(-1);
        }

        if(guess.length() > 1) {
            if(guess == word) {
                if(end_game(true, true)) {
                    play_round();
                }
            } else {
                if(end_game(false)) {
                    play_round();
                }
            }
            return;
        }

        std::transform(guess.begin(), guess.end(), guess.begin(), ::tolower);
        if(std::find(guesses.begin(), guesses.end(), guess[0]) != guesses.end()) {
            std::cout << "You already guessed letter \"" << guess << "\"." << std::endl;
            continue;
        } else if(guess.length() > 0) {
            temp = view;
            std::transform(word.begin(), word.end(), view.begin(), view.begin(), replacer(guess[0]));
            std::transform(word.begin(), word.end(), view.begin(), view.begin(), replacer(toupper(guess[0])));
            if(view.find("-") == std::string::npos) {
                if(end_game(true)) {
                    play_round();
                }
                return;
            }
            guesses.push_back(guess[0]);
            if(temp == view) {
                if(--lives <= 0) {
                    if(end_game(false)) {
                        play_round();
                    }
                    return;
                }
            }
        }
    }
}

// print filtered wordlist to file
void Hangman::output_wordlist() {
    std::ofstream outfile("gamewords", std::ios::out);
    std::ostream_iterator<std::string> outfile_iterator(outfile, "\n");
    std::copy(wordlist.begin(), wordlist.end(), outfile_iterator);
}

// print the current game state
void Hangman::print_state() {
    std::cout << "Word: " << view << std::endl;
    std::cout << "Letters used:";
    std::for_each(guesses.begin(), guesses.end(), print_item);
    std::cout << std::endl;
    std::cout << "You have " << lives << " ";
    if(lives == 1) {
        std::cout << "life";
    } else {
        std::cout << "lives";
    }
    std::cout << " left." << std::endl;
}


int main(int argc, char* argv[]) {
    if(argc < 2) {
        std::cout << "Error: No input file specified." << std::endl;
        exit(-1);
    }
    int seed = (argc >= 3) ? atoi(argv[2]) : 0;
    srand48(seed);

    Hangman* game = new Hangman(argv[1]);
    game->output_wordlist();
    game->play_round();

    delete game;
    return 0;
}









