#define DEBUG

#include <iostream>
#include <sstream>
#include <memory>

#include "gtest/gtest.h"

#include "UserAccount.h"


class AccountTest : public ::testing::Test {
    void reset() {
        user0.reset();
        user1.reset();
    }
public:
    std::auto_ptr<UserAccount> user0;
    std::auto_ptr<UserAccount> user1;

    std::ostringstream oss;
    std::streambuf* backup;

    virtual void SetUp() {
        reset();

        backup = std::cout.rdbuf();
        std::cout.rdbuf(oss.rdbuf());
    }

    virtual void TearDown() {
        reset();

        std::cout.rdbuf(backup);
    }
};

TEST_F(AccountTest, ConstructorValid) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());

    EXPECT_EQ(oss.str(), "Enter preferred userid: Enter preferred password: ");
}

TEST_F(AccountTest, ConstructorDuplicateAccountID) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());

    input.str("AccountID\nAccountID2\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user1.reset(new UserAccount());
    EXPECT_EQ(oss.str(), "Enter preferred userid: Userid \"AccountID\" already exists.  Please try again.\nEnter preferred userid: Enter preferred password: ");
}

TEST_F(AccountTest, ConstructorInvalidPassword) {
    std::stringstream input("AccountID\npasswor\npassword\nPAssword\nPA55word\nPA55word!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    EXPECT_EQ(oss.str(), "Enter preferred userid: Enter preferred password: Password :\n    - must be at least 8 characters\n    - must include at least 2 capital letters\n    - must include at least 2 digits\n    - must include at least 1 symbol\nEnter preferred password: Password :\n    - must include at least 2 capital letters\n    - must include at least 2 digits\n    - must include at least 1 symbol\nEnter preferred password: Password :\n    - must include at least 2 digits\n    - must include at least 1 symbol\nEnter preferred password: Password :\n    - must include at least 1 symbol\nEnter preferred password: ");
}

TEST_F(AccountTest, ConstructorActive) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    EXPECT_FALSE(user0.get()->check_deactivated());
}

TEST_F(AccountTest, Destructor) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());
    user0.reset();

    input.str("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user1.reset(new UserAccount());
    EXPECT_EQ(oss.str(), "Enter preferred userid: Enter preferred password: ");
}

TEST_F(AccountTest, AuthenticateCorrect) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());

    input.str("P4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.get()->authenticate();
    EXPECT_EQ(oss.str(), "Enter password: ");
}

TEST_F(AccountTest, AuthenticateDeactivated) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    user0.get()->deactivate();
    user0.get()->authenticate();

    EXPECT_EQ(oss.str(), "Enter preferred userid: Enter preferred password: Account has been deactivated.\n");
}

TEST_F(AccountTest, AuthenticateImposter) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());

    input.str("wrong\nwrong\nwrong\n");
    std::cin.rdbuf(input.rdbuf());

    user0.get()->authenticate();
    EXPECT_EQ(oss.str(), "Enter password: Invalid password. You have 2 tries to get it right.\nEnter password: Invalid password. You have 1 tries to get it right.\nEnter password: Imposter!! Account is being deactivated!!\n");
}

TEST_F(AccountTest, AuthenticateImposterDeactivates) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());

    input.str("wrong\nwrong\nwrong\n");
    std::cin.rdbuf(input.rdbuf());

    user0.get()->authenticate();
    EXPECT_EQ(oss.str(), "Enter password: Invalid password. You have 2 tries to get it right.\nEnter password: Invalid password. You have 1 tries to get it right.\nEnter password: Imposter!! Account is being deactivated!!\n");
    EXPECT_TRUE(user0.get()->check_deactivated());
}

TEST_F(AccountTest, AuthenticateIncorrect) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    oss.str(std::string());

    input.str("wrong\nwrong\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.get()->authenticate();
    EXPECT_EQ(oss.str(), "Enter password: Invalid password. You have 2 tries to get it right.\nEnter password: Invalid password. You have 1 tries to get it right.\nEnter password: ");
}

TEST_F(AccountTest, Deactivate) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    user0.get()->deactivate();
    EXPECT_TRUE(user0.get()->check_deactivated());
}

TEST_F(AccountTest, Reactivate) {
    std::stringstream input("AccountID\nP4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());

    user0.reset(new UserAccount());
    user0.get()->deactivate();

    input.str("P4SSw0rd!\n");
    std::cin.rdbuf(input.rdbuf());
    user0.get()->reactivate();

    EXPECT_EQ(oss.str(), "Enter preferred userid: Enter preferred password: Enter preferred password: ");
    EXPECT_FALSE(user0.get()->check_deactivated());
}
