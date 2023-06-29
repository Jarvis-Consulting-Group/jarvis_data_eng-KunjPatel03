-- Q: The club is adding a new facility - a spa. We need to add it into the facilities table. Use values: facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.
INSERT INTO cd.facilities 
VALUES 
  (9, 'Spa', 20, 30, 100000, 800);
-- Let's try adding the spa to the facilities table again. This time, though, we want to automatically generate the value for the next facid, rather than specifying it as a constant. Use the following values for everything else: Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.
INSERT INTO cd.facilities 
VALUES 
  (
    (
      SELECT 
        MAX(facid) 
      FROM 
        cd.facilities
    )+ 1, 
    'Spa', 
    20, 
    30, 
    100000, 
    800
  );
-- Q: We made a mistake when entering the data for the second tennis court. The initial outlay was 10000 rather than 8000: you need to alter the data to fix the error.
UPDATE 
  cd.facilities 
SET 
  initialoutlay = 10000 
WHERE 
  facid = 1;
-- Q: We want to alter the price of the second tennis court so that it costs 10% more than the first one. Try to do this without using constant values for the prices, so that we can reuse the statement if we want to.
UPDATE 
  cd.facilities 
SET 
  membercost = (
    SELECT 
      membercost 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ) * 1.1, 
  guestcost = (
    SELECT 
      guestcost 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ) * 1.1 
WHERE 
  facid = 1; 
  
-- Q: As part of a clearout of our database, we want to delete all bookings from the cd.bookings table. How can we accomplish this?
  TRUNCATE cd.bookings;

-- Q: We want to remove member 37, who has never made a booking, from our database. How can we achieve that?
DELETE FROM 
  cd.members 
WHERE 
  memid = 37;

-- Q: How can you produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost? Return the facid, facility name, member cost, and monthly maintenance of the facilities in question.
SELECT 
  facid, 
  name, 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost < monthlymaintenance / 50 
  AND membercost > 0;
-- How can you produce a list of all facilities with the word 'Tennis' in their name?'
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  name LIKE '%Tennis%';
-- How can you retrieve the details of facilities with ID 1 and 5? Try to do it without using the OR operator.
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  facid IN (1, 5);
-- How can you produce a list of members who joined after the start of September 2012? Return the memid, surname, firstname, and joindate of the members in question.
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate >= '2012-09-01';
-- You, for some reason, want a combined list of all surnames and all facility names. 
SELECT 
  surname 
FROM 
  cd.members 
UNION 
SELECT 
  name 
FROM 
  cd.facilities;
-- How can you produce a list of the start times for bookings by members named 'David Farrell'?
SELECT 
  bs.starttime 
FROM 
  cd.bookings as bs 
  JOIN cd.members as ms ON ms.memid = bs.memid 
WHERE 
  ms.firstname = 'David' 
  and ms.surname = 'Farrell';
-- How can you produce a list of the start times for bookings for tennis courts, for the date '2012-09-21'? Return a list of start time and facility name pairings, ordered by the time.
SELECT 
  bs.starttime, 
  fa.name 
FROM 
  cd.bookings AS bs 
  JOIN cd.facilities AS fa ON bs.facid = fa.facid 
WHERE 
  bs.starttime BETWEEN '2012-09-21' 
  AND '2012-09-22' 
  AND fa.name IN (
    'Tennis Court 1', 'Tennis Court 2'
  ) 
ORDER BY 
  bs.starttime;
-- How can you output a list of all members, including the individual who recommended them (if any)? Ensure that results are ordered by (surname, firstname).
SELECT 
  ms.firstname AS memfname, 
  ms.surname AS memsname, 
  rs.firstname AS recfname, 
  rs.surname AS recsname 
FROM 
  cd.members AS ms 
  LEFT OUTER JOIN cd.members AS rs ON rs.memid = ms.recommendedby 
ORDER BY 
  memsname, 
  memfname;
-- How can you output a list of all members who have recommended another member? Ensure that there are no duplicates in the list, and that results are ordered by (surname, firstname).
SELECT 
  DISTINCT rs.firstname, 
  rs.surname 
FROM 
  cd.members as ms 
  INNER JOIN cd.members AS rs ON rs.memid = ms.recommendedby 
ORDER BY 
  rs.surname, 
  rs.firstname;
-- How can you output a list of all members, including the individual who recommended them (if any), without using any joins? Ensure that there are no duplicates in the list, and that each firstname + surname pairing is formatted as a column and ordered.
SELECT 
  DISTINCT ms.firstname || ' ' || ms.surname AS member, 
  (
    SELECT 
      rs.firstname || ' ' || rs.surname 
    FROM 
      cd.members AS rs 
    WHERE 
      memid = ms.recommendedby
  ) AS recommender 
FROM 
  cd.members AS ms 
ORDER BY 
  member
 -- Produce a count of the number of recommendations each member has made. Order by member ID.
SELECT 
  recommendedby, 
  count(*) 
FROM 
  cd.members 
WHERE 
  recommendedby IS NOT NULL 
GROUP BY 
  recommendedby 
ORDER BY 
  recommendedby;
-- Produce a list of the total number of slots booked per facility. For now, just produce an output table consisting of facility id and slots, sorted by facility id.
SELECT 
  facid, 
  sum(slots) AS Total_Slots 
FROM 
  cd.bookings 
GROUP BY 
  facid 
ORDER BY 
  facid;
-- Produce a list of the total number of slots booked per facility in the month of September 2012. Produce an output table consisting of facility id and slots, sorted by the number of slots.
SELECT 
  facid, 
  sum(slots) AS Total_Slots 
FROM 
  cd.bookings 
WHERE 
  starttime >= '2012-09-01' 
  AND starttime < '2012-10-01' 
GROUP BY 
  facid 
ORDER BY 
  Total_Slots;
-- Produce a list of the total number of slots booked per facility per month in the year of 2012. Produce an output table consisting of facility id and slots, sorted by the id and month.
SELECT 
  facid, 
  extract(
    month 
    FROM 
      starttime
  ) AS month, 
  sum(slots) AS "Total Slots" 
FROM 
  cd.bookings 
WHERE 
  extract(
    year 
    FROM 
      starttime
  ) = 2012 
GROUP BY 
  facid, 
  month 
ORDER BY 
  facid, 
  month;
-- Find the total number of members (including guests) who have made at least one booking.
SELECT 
  COUNT(DISTINCT memid) 
FROM 
  cd.bookings;
-- Produce a list of each member name, id, and their first booking after September 1st 2012. Order by member ID.
SELECT 
  ms.surname, 
  ms.firstname, 
  ms.memid, 
  min(bs.starttime) 
FROM 
  cd.members AS ms 
  JOIN cd.bookings AS bs ON ms.memid = bs.memid 
WHERE 
  bs.starttime >= '2012-09-01' 
GROUP BY 
  ms.surname, 
  ms.firstname, 
  ms.memid 
ORDER BY 
  ms.memid;
-- Produce a list of member names, with each row containing the total member count. Order by join date, and include guest members.
SELECT 
  COUNT(*) OVER(), 
  firstname, 
  surname 
FROM 
  cd.members 
ORDER BY 
  joindate;
-- Produce a monotonically increasing numbered list of members (including guests), ordered by their date of joining. Remember that member IDs are not guaranteed to be sequential.
SELECT 
  ROW_NUMBER() OVER(
    ORDER BY 
      joindate
  ), 
  firstname, 
  surname 
FROM 
  cd.members 
ORDER BY 
  joindate;
-- Output the facility id that has the highest number of slots booked. Ensure that in the event of a tie, all tieing results get output.
SELECT 
  facid, 
  total 
FROM 
  (
    SELECT 
      facid, 
      SUM(slots) AS total, 
      RANK() OVER (
        ORDER BY 
          SUM(slots) DESC
      ) AS rank 
    FROM 
      cd.bookings 
    GROUP BY 
      facid
  ) AS ranked 
WHERE 
  rank = 1;
-- Output the names of all members, formatted as 'Surname, Firstname'
SELECT 
  surname || ', ' || firstname AS name 
FROM 
  cd.members;
-- You've noticed that the club's member table has telephone numbers with very inconsistent formatting. You'd like to find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
SELECT 
  memid, 
  telephone 
FROM 
  cd.members 
WHERE 
  telephone ~ '[()]';
-- You'd like to produce a count of how many members you have whose surname starts with each letter of the alphabet. Sort by the letter, and don't worry about printing out a letter if the count is 0.
SELECT 
  SUBSTR(mems.surname, 1, 1) AS letter, 
  COUNT(*) AS count 
FROM 
  cd.members mems 
GROUP BY 
  letter 
ORDER BY 
  letter;
