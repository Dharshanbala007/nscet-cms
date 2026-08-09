-- Additional staff data (STF026–STF055)
-- Dept IDs: 1=MECH,2=CE,3=ECE,4=CSE,5=ACCT,6=ADMIN,7=SYS,8=TRANS,9=LIB,10=CAN,11=PHY,12=CHEM,13=MATH,14=PD,15=ENG,16=TPO,17=SH,18=EEE,19=ME,20=IT,21=AI,22=SE,23=TAM,24=EST,25=MECS
-- Desig IDs: 1=AP,2=HOD,3=Associate Professor,4=Library,5=PD,6=P.Co-ordinator,7=Teaching Fellow,8=Dept Incharge,9=EOC
INSERT INTO admin_staff_master (staff_code, name, address, city, pin_code, date_of_birth, category, department_id, designation_id, staff_group, college_code, transport, email, pf_active, sex, date_of_joining, phone, blood_group, aadhar_number) VALUES
-- CSE Department (4)
('STF026', 'Dr. A. Selvakumar', '14, Rajaji Street', 'Theni', '625531', '1976-04-12', 'OC', 4, 2, 'Teaching', 'NSCET', 'College', 'selvakumar@nscet.edu', TRUE, 'M', '2007-06-15', '9876543226', 'O+', '123456789026'),
('STF027', 'Ms. R. Suganya', '38, Velammal Nagar', 'Theni', '625531', '1993-01-15', 'BC', 4, 1, 'Teaching', 'NSCET', 'Own', 'suganya@nscet.edu', TRUE, 'F', '2020-01-10', '9876543227', 'B+', '123456789027'),
('STF028', 'Mr. V. Prabhakaran', '55, Ambedkar Street', 'Theni', '625531', '1984-09-20', 'MBC', 4, 1, 'Teaching', 'NSCET', 'College', 'prabhakaran@nscet.edu', TRUE, 'M', '2014-07-01', '9876543228', 'A-', '123456789028'),
('STF029', 'Dr. K. Balachandar', '72, Nehru Nagar', 'Theni', '625531', '1973-11-05', 'OC', 4, 3, 'Teaching', 'NSCET', 'College', 'balachandar@nscet.edu', TRUE, 'M', '2003-06-15', '9876543229', 'AB+', '123456789029'),
('STF030', 'Ms. T. Meenatchi', '29, Gandhi Road', 'Theni', '625531', '1991-07-22', 'SC', 4, 1, 'Teaching', 'NSCET', 'Own', 'meenatchi@nscet.edu', TRUE, 'F', '2018-06-01', '9876543230', 'O-', '123456789030'),
-- ECE Department (3)
('STF031', 'Dr. S. Radhakrishnan', '18, Kamaraj Salai', 'Theni', '625531', '1971-03-18', 'OC', 3, 2, 'Teaching', 'NSCET', 'College', 'radhakrishnan@nscet.edu', TRUE, 'M', '2001-06-15', '9876543231', 'B+', '123456789031'),
('STF032', 'Mr. D. Senthilkumar', '47, VOC Street', 'Theni', '625531', '1986-12-09', 'BC', 3, 1, 'Teaching', 'NSCET', 'College', 'senthilkumar.ece@nscet.edu', TRUE, 'M', '2015-01-15', '9876543232', 'A+', '123456789032'),
('STF033', 'Ms. G. Priyadharshini', '63, MGR Nagar', 'Theni', '625531', '1994-05-14', 'MBC', 3, 1, 'Teaching', 'NSCET', 'Own', 'priyadharshini@nscet.edu', TRUE, 'F', '2021-01-10', '9876543233', 'B-', '123456789033'),
('STF034', 'Dr. M. Vijayakumar', '36, Thiruvalluvar Street', 'Theni', '625531', '1975-08-28', 'OC', 3, 3, 'Teaching', 'NSCET', 'College', 'vijayakumar@nscet.edu', TRUE, 'M', '2006-06-15', '9876543234', 'O+', '123456789034'),
-- MECH Department (1)
('STF035', 'Dr. P. Senthilnathan', '81, Kumaran Nagar', 'Theni', '625531', '1969-06-11', 'OC', 1, 2, 'Teaching', 'NSCET', 'College', 'senthilnathan@nscet.edu', TRUE, 'M', '2000-06-15', '9876543235', 'AB-', '123456789035'),
('STF036', 'Mr. K. Anandh', '22, Saravana Nagar', 'Theni', '625531', '1988-10-03', 'BC', 1, 1, 'Teaching', 'NSCET', 'College', 'anandh@nscet.edu', TRUE, 'M', '2016-07-01', '9876543236', 'A+', '123456789036'),
('STF037', 'Ms. L. Deepika', '49, Annai Nagar', 'Theni', '625531', '1992-02-25', 'MBC', 1, 1, 'Teaching', 'NSCET', 'Own', 'deepika@nscet.edu', TRUE, 'F', '2019-01-15', '9876543237', 'B+', '123456789037'),
('STF038', 'Dr. R. Murugesh', '15, Rajaji Nagar', 'Theni', '625531', '1974-07-17', 'OC', 1, 3, 'Teaching', 'NSCET', 'College', 'murugesh@nscet.edu', TRUE, 'M', '2005-06-15', '9876543238', 'O-', '123456789038'),
-- CE Department (2)
('STF039', 'Dr. B. Senthilraj', '58, Bharathi Nagar', 'Theni', '625531', '1972-09-09', 'OC', 2, 2, 'Teaching', 'NSCET', 'College', 'senthilraj@nscet.edu', TRUE, 'M', '2002-06-15', '9876543239', 'A-', '123456789039'),
('STF040', 'Mr. S. Arunkumar', '33, Sivan Kovil Street', 'Theni', '625531', '1985-04-30', 'SC', 2, 1, 'Teaching', 'NSCET', 'Own', 'arunkumar@nscet.edu', TRUE, 'M', '2013-07-01', '9876543240', 'B+', '123456789040'),
('STF041', 'Ms. P. Kavitha', '76, Jothi Nagar', 'Theni', '625531', '1990-12-18', 'BC', 2, 1, 'Teaching', 'NSCET', 'Own', 'kavitha.civil@nscet.edu', TRUE, 'F', '2017-01-15', '9876543241', 'AB+', '123456789041'),
-- EEE Department (18)
('STF042', 'Dr. T. Ganesan', '44, Anna Nagar', 'Theni', '625531', '1977-02-14', 'OC', 18, 2, 'Teaching', 'NSCET', 'College', 'ganesan@nscet.edu', TRUE, 'M', '2008-06-15', '9876543242', 'O+', '123456789042'),
('STF043', 'Mr. N. Muruganantham', '61, Periyar Street', 'Theni', '625531', '1989-08-07', 'MBC', 18, 1, 'Teaching', 'NSCET', 'College', 'muruganantham@nscet.edu', TRUE, 'M', '2017-07-01', '9876543243', 'A+', '123456789043'),
('STF044', 'Ms. S. Revathi', '27, Sakthi Nagar', 'Theni', '625531', '1993-11-21', 'OC', 18, 1, 'Teaching', 'NSCET', 'Own', 'revathi.eee@nscet.edu', TRUE, 'F', '2020-01-10', '9876543244', 'B-', '123456789044'),
-- IT Department (20)
('STF045', 'Dr. M. Dhanapal', '52, Subramaniapuram', 'Theni', '625531', '1978-05-26', 'MBC', 20, 2, 'Teaching', 'NSCET', 'College', 'dhanapal@nscet.edu', TRUE, 'M', '2009-06-15', '9876543245', 'B+', '123456789045'),
('STF046', 'Mr. R. Yogaraj', '19, Gandhi Maidan', 'Theni', '625531', '1987-03-14', 'OC', 20, 1, 'Teaching', 'NSCET', 'College', 'yogaraj@nscet.edu', TRUE, 'M', '2015-01-15', '9876543246', 'AB-', '123456789046'),
('STF047', 'Ms. P. Nithya', '85, Lakshmi Puram', 'Theni', '625531', '1995-09-03', 'BC', 20, 1, 'Teaching', 'NSCET', 'Own', 'nithya@nscet.edu', TRUE, 'F', '2022-01-10', '9876543247', 'A+', '123456789047'),
-- AI Department (21)
('STF048', 'Dr. V. Mohanraj', '31, Temple Road', 'Theni', '625531', '1979-12-01', 'OC', 21, 2, 'Teaching', 'NSCET', 'College', 'mohanraj@nscet.edu', TRUE, 'M', '2010-06-15', '9876543248', 'O+', '123456789048'),
('STF049', 'Ms. A. Kowsalya', '46, MGR Nagar', 'Theni', '625531', '1994-06-17', 'SC', 21, 1, 'Teaching', 'NSCET', 'Own', 'kowsalya@nscet.edu', TRUE, 'F', '2021-07-01', '9876543249', 'B+', '123456789049'),
-- Non-Teaching / Office Staff
('STF050', 'Mr. S. Ramakrishnan', '57, VOC Colony', 'Theni', '625531', '1981-04-08', 'OC', 5, 1, 'Office', 'NSCET', 'College', 'ramakrishnan@nscet.edu', TRUE, 'M', '2010-06-15', '9876543250', 'AB+', '123456789050'),
('STF051', 'Mrs. V. Mahalakshmi', '39, Ambedkar Nagar', 'Theni', '625531', '1986-10-29', 'BC', 6, 7, 'Office', 'NSCET', 'Own', 'mahalakshmi@nscet.edu', TRUE, 'F', '2014-01-15', '9876543251', 'B-', '123456789051'),
('STF052', 'Mr. K. Sivakumar', '68, Kumaran Nagar', 'Theni', '625531', '1979-07-11', 'MBC', 8, 7, 'Office', 'NSCET', 'College', 'sivakumar.transport@nscet.edu', TRUE, 'M', '2009-08-01', '9876543252', 'O+', '123456789052'),
('STF053', 'Mr. R. Selvam', '12, Rajaji Street', 'Theni', '625531', '1983-01-23', 'SC', 10, 7, 'Office', 'NSCET', 'Own', 'selvam@nscet.edu', FALSE, 'M', '2011-04-01', '9876543253', 'A-', '123456789053'),
('STF054', 'Mrs. D. Kokila', '83, Annai Nagar', 'Theni', '625531', '1990-03-15', 'OC', 9, 4, 'Office', 'NSCET', 'Own', 'kokila@nscet.edu', TRUE, 'F', '2018-06-15', '9876543254', 'B+', '123456789054'),
('STF055', 'Mr. M. Karthikeyan', '41, Bharathi Nagar', 'Theni', '625531', '1982-08-19', 'BC', 7, 5, 'Office', 'NSCET', 'College', 'karthikeyan@nscet.edu', TRUE, 'M', '2012-03-01', '9876543255', 'AB-', '123456789055');
