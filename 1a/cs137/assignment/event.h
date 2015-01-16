#ifndef EVENT_H
#define EVENT_H

struct tod {
  int hour;
  int minute;
};

struct event {
  struct tod start;
  struct tod end; // not inclusive
};

#endif
