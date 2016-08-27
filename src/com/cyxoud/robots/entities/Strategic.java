package com.cyxoud.robots.entities;

import java.util.List;

/**
 * Represents actions of robot due to strategy
 */
interface Strategic {
    Runnable strategy();
}
