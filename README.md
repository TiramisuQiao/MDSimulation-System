# Molecular Dynamics Simulation of Hard Spheres

## Introduction

This is the Midterm Project for **Midterm Project of CS1501 Algorithm Implementation**.
Please Note that DO NOT plagiarize this assignment, it is better for you and me.
Any suggestions is welcome to show.

## TO-DO List

1. - [x] Brownian motion
2. - [x] Free path and free time
3. - [x] Collision frequency
4. - [ ] Root mean-square velocity
5. - [ ] Maxwell-Boltzmann distribution
6. - [ ] Pressure
7. - [ ] Temperature
8. - [ ] Diffusion
9. - [ ] Time reversibility
10. - [ ] Maxwell's demon

## Things After Computation

### Brownian motion

In 1827, the botanist Robert Brown observed the motion of wildflower pollen grains immersed 
in water using a microscope. He observed that the pollen grains were in a random motion, following 
what would become known as Brownian motion. This phenomenon was discussed, but no convincing 
explanation was provided until Einstein provided a mathematical one in 1905. 
Einstein's explanation: the motion of the pollen grain particles was caused by millions of 
tiny molecules colliding with the larger particles. He gave detailed formulas describing the 
behavior of tiny particles suspended in a liquid at a given temperature. Einstein's explanation 
was intended to help justify the existence of atoms and molecules and could be used to estimate 
the size of the molecules. Einstein's theory of Brownian motion enables engineers to compute the 
diffusion constants of alloys by observing the motion of individual particles.

It can be seen from the images:
(Using the parameter: draw = true, drawFreq = 2, drawPause = 20, dataset = brownian.txt,
boxSize = 10, scaleX = [0,10], scaleY = [0,10],t_max = 4000)

![fig-1-1.png](img/fig-1-1.png)

And Also refer to the gifs:
![new.gif](img/new.gif)

Our simulation successfully replicates the key features of Brownian motion described by Einstein.
The consistency with expected random motion and diffusion patterns confirms the validity of 
our simulation.

### Free path and free time
Free path = distance a particle travels between collisions. Plot histogram. 
Mean free path = average free path length over all particles. As temperature 
increases, mean free path increases (holding pressure constant). Compute free 
time length = time elapsed before a particle collides with another particle 
or wall.

We can store the distance interval between two events, also the time intervals.
Evaluating the Collision with brownian dataset, Boxsize is 10 in t_max 4000.

Mean Free Path: 9.628867535876509E-4

Mean Free Time: 0.010477422440390472

### Collision frequency

Number of collisions per second

We can store the time interval between two events, then we can calculate the SUM
of the intervals. Then it can be calculated as:

f = N / sum(delta t)

Where N is the number of intervals and delta t is each interval length.

Evaluating the Collision Frequency with brownian dataset, BoxSize is 10 in t_max 4000,
The Collision frequency is 7.050855584767143

### Root mean-square velocity

### Maxwell-Boltzmann distribution

### Pressure

### Temperature

### Diffusion

### Time reversibility

### Maxwell's demon
