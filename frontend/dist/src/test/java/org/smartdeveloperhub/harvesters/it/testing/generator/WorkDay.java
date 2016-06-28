package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.util.Iterator;
import java.util.Random;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

final class WorkDay {

	private final class WorkingHourIterator implements Iterator<LocalTime> {

		private LocalTime start;

		private WorkingHourIterator(final LocalTime workDayStart) {
			this.start = workDayStart;
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public LocalTime next() {
			final LocalTime current = this.start;
			this.start=this.start.plusMinutes(3+WorkDay.this.random.nextInt(12)+WorkDay.this.random.nextInt(60)%5);
			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Working hours cannot be removed");
		}
	}

	private final Random random;
	private final LocalTime start;
	private final LocalTime end;

	private final Minutes duration;

	WorkDay(final Random random) {
		this.random = random;
		this.start=new LocalTime(8,0);
		this.end=new LocalTime(20,0);
		this.duration=Minutes.minutes(60*6+random.nextInt(30*6));
	}

	int workingHoursPerDay() {
		return this.end.getHourOfDay()-this.start.getHourOfDay();
	}

	int effortPerDay() {
		return this.duration.getMinutes();
	}

	LocalTime workingHour() {
		return this.start.plusHours(this.random.nextInt(workingHoursPerDay()));
	}

	LocalTime workingTime() {
		return adjustedStartTime().plusMinutes(this.random.nextInt(workingHoursPerDay()*60));
	}

	public Iterator<LocalTime> workingTimes() {
		return new WorkingHourIterator(adjustedStartTime());
	}

	private LocalTime adjustedStartTime() {
		LocalTime currentStart=null;
		final int adjustCase = this.random.nextInt(100)%3;
		if(adjustCase==0) {
			currentStart=this.start;
		} else if(adjustCase==1) {
			currentStart=this.start.minusMinutes(30+this.random.nextInt(30*4));
		} else {
			currentStart=this.start.plusMinutes(30+this.random.nextInt(30*4));
		}
		return currentStart;
	}

	@Override
	public String toString() {
		return String.format("Work day starts at %s and ends at %s. ~%1.1f hours of work expected.", this.start,this.end,effortPerDay()/60.0f);
	}

}