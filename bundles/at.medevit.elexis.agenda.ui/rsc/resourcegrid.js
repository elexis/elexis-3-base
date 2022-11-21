var FC = $.fullCalendar; // a reference to FullCalendar's root namespace
var Grid = FC.Grid;
var divideDurationByDuration = FC.divideDurationByDuration;
var isInt = FC.isInt;
var htmlEscape = FC.htmlEscape;
var CoordCache = FC.CoordCache;
var intersectRanges = FC.intersectRanges;
var cssToStr = FC.cssToStr;
var proxy = FC.proxy;
var durationHasTime = FC.durationHasTime;
var pluckEventDateProps = FC.pluckEventDateProps;

// potential nice values for the slot-duration and interval-duration
// from largest to smallest
var AGENDA_STOCK_SUB_DURATIONS = [{hours: 1}, {minutes: 30}, {minutes: 15}, {seconds: 30}, {seconds: 15}];

/* A component that renders one or more columns of vertical time slots
----------------------------------------------------------------------------------------------------------------------*/
// We mixin DayTable, even though there is only a single row of days

var ResourceGrid = (FC.ResourceGrid = Grid.extend({
  resourceIds: null,
  slotDuration: null, // duration of a "slot", a distinct time segment on given day, visualized by lines
  snapDuration: null, // granularity of time for dragging and selecting
  snapsPerSlot: null,
  minTime: null, // Duration object that denotes the first visible time of any given day
  maxTime: null, // Duration object that denotes the exclusive visible end time of any given day
  labelFormat: null, // formatting string for times running along vertical axis
  labelInterval: null, // duration of how often a label should be displayed for a slot

  colEls: null, // cells elements in the day-row background
  slatContainerEl: null, // div that wraps all the slat rows
  slatEls: null, // elements running horizontally across all columns
  nowIndicatorEls: null,

  colCoordCache: null,
  slatCoordCache: null,

  colCnt: null,

  constructor: function () {
    Grid.apply(this, arguments); // call the super-constructor
    this.setResourceIds(['Arifi (cs)', 'Apel', 'Bauer']);
    this.processOptions();
  },

  setResourceIds: function (ids) {
    if (ids != null) {
      this.resourceIds = ids.slice(0);
      this.colCnt = this.resourceIds.length;
    } else {
      this.resourceIds = [];
      this.colCnt = 0;
    }
  },

  // Renders the time grid into `this.el`, which should already be assigned.
  // Relies on the view's colCnt. In the future, this component should probably be self-sufficient.
  renderDates: function () {
    this.el.html(this.renderHtml());
    this.colEls = this.el.find('.fc-day');
    this.slatContainerEl = this.el.find('.fc-slats');
    this.slatEls = this.slatContainerEl.find('tr');

    this.colCoordCache = new CoordCache({
      els: this.colEls,
      isHorizontal: true,
    });
    this.slatCoordCache = new CoordCache({
      els: this.slatEls,
      isVertical: true,
    });

    this.renderContentSkeleton();
  },

  // Renders the basic HTML skeleton for the grid
  renderHtml: function () {
    return (
      '' +
      '<div class="fc-bg">' +
      '<table>' +
      this.renderBgTrHtml(0) + // row=0
      '</table>' +
      '</div>' +
      '<div class="fc-slats">' +
      '<table>' +
      this.renderSlatRowHtml() +
      '</table>' +
      '</div>'
    );
  },

  // Generates the HTML that will go before the day-of week header cells
  renderHeadIntroHtml: function () {
    var view = this.view;
    var weekText;

    if (view.opt('weekNumbers')) {
      weekText = this.start.format(view.opt('smallWeekFormat'));

      return (
        '' +
        '<th class="fc-axis fc-week-number ' +
        view.widgetHeaderClass +
        '" ' +
        view.axisStyleAttr() +
        '>' +
        view.buildGotoAnchorHtml(
          // aside from link, important for matchCellWidths
          {date: this.start, type: 'week', forceOff: this.colCnt > 1},
          htmlEscape(weekText) // inner HTML
        ) +
        '</th>'
      );
    } else {
      return '<th class="fc-axis ' + view.widgetHeaderClass + '" ' + view.axisStyleAttr() + '></th>';
    }
  },

  // Generates the HTML that goes before the bg of the TimeGrid slot area. Long vertical column.
  renderBgIntroHtml: function () {
    var view = this.view;

    return '<td class="fc-axis ' + view.widgetContentClass + '" ' + view.axisStyleAttr() + '></td>';
  },

  // Generates the HTML that goes before all other types of cells.
  // Affects content-skeleton, helper-skeleton, highlight-skeleton for both the time-grid and day-grid.
  renderIntroHtml: function () {
    var view = this.view;

    return '<td class="fc-axis" ' + view.axisStyleAttr() + '></td>';
  },

  // Generates the HTML for the horizontal "slats" that run width-wise. Has a time axis on a side. Depends on RTL.
  renderSlatRowHtml: function () {
    var view = this.view;
    var isRTL = this.isRTL;
    var html = '';
    var slotTime = moment.duration(+this.minTime); // wish there was .clone() for durations
    var slotDate; // will be on the view's first day, but we only care about its time
    var isLabeled;
    var axisHtml;

    // Calculate the time for each slot
    while (slotTime < this.maxTime) {
      slotDate = this.start.clone().time(slotTime);
      isLabeled = isInt(divideDurationByDuration(slotTime, this.labelInterval));

      axisHtml =
        '<td class="fc-axis fc-time ' +
        view.widgetContentClass +
        '" ' +
        view.axisStyleAttr() +
        '>' +
        (isLabeled
          ? '<span>' + // for matchCellWidths
            htmlEscape(slotDate.format(this.labelFormat)) +
            '</span>'
          : '') +
        '</td>';

      html +=
        '<tr data-time="' +
        slotDate.format('HH:mm:ss') +
        '"' +
        (isLabeled ? '' : ' class="fc-minor"') +
        '>' +
        (!isRTL ? axisHtml : '') +
        '<td class="' +
        view.widgetContentClass +
        '"/>' +
        (isRTL ? axisHtml : '') +
        '</tr>';

      slotTime.add(this.slotDuration);
    }

    return html;
  },

  renderHeadHtml: function () {
    var view = this.view;

    return (
      '' +
      '<div class="fc-row ' +
      view.widgetHeaderClass +
      '">' +
      '<table>' +
      '<thead>' +
      this.renderHeadTrHtml() +
      '</thead>' +
      '</table>' +
      '</div>'
    );
  },

  renderHeadTrHtml: function () {
    return (
      '' +
      '<tr>' +
      (this.isRTL ? '' : this.renderHeadIntroHtml()) +
      this.renderHeadCellsHtml() +
      (this.isRTL ? this.renderHeadIntroHtml() : '') +
      '</tr>'
    );
  },

  renderHeadCellsHtml: function () {
    var htmls = [];
    var index;

    if (this.resourceIds != null) {
      for (index = 0; index < this.resourceIds.length; index++) {
        htmls.push(this.renderHeadCellHtml(this.resourceIds[index]));
      }
    }

    return htmls.join('');
  },

  renderHeadCellHtml: function (resource) {
    var view = this.view;
    var classNames = ['fc-day-header', view.widgetHeaderClass];

    return (
      '' +
      '<th class="' +
      classNames.join(' ') +
      '"' +
      '>' +
      htmlEscape(resource) + // inner HTML
      '</th>'
    );
  },

  /* Background Rendering
	------------------------------------------------------------------------------------------------------------------*/

  renderBgTrHtml: function (row) {
    return (
      '' +
      '<tr>' +
      (this.isRTL ? '' : this.renderBgIntroHtml(row)) +
      this.renderBgCellsHtml(row) +
      (this.isRTL ? this.renderBgIntroHtml(row) : '') +
      '</tr>'
    );
  },

  renderBgIntroHtml: function (row) {
    return this.renderIntroHtml(); // fall back to generic
  },

  renderBgCellsHtml: function (row) {
    var htmls = [];
    var col, date;

    for (col = 0; col < this.colCnt; col++) {
      date = this.getCellDate(row, col);
      htmls.push(this.renderBgCellHtml(date, ' data-resource="' + this.resourceIds[col] + '"'));
    }

    return htmls.join('');
  },

  renderBgCellHtml: function (date, otherAttrs) {
    var view = this.view;
    var classes = this.getDayClasses(date);

    classes.unshift('fc-day', view.widgetContentClass);

    return (
      '<td class="' +
      classes.join(' ') +
      '"' +
      ' data-date="' +
      date.format('YYYY-MM-DD') +
      '"' + // if date has a time, won't format it
      (otherAttrs ? ' ' + otherAttrs : '') +
      '></td>'
    );
  },

  /* Options
	------------------------------------------------------------------------------------------------------------------*/

  // Parses various options into properties of this object
  processOptions: function () {
    var view = this.view;
    var slotDuration = view.opt('slotDuration');
    var snapDuration = view.opt('snapDuration');
    var input;

    slotDuration = moment.duration(slotDuration);
    snapDuration = snapDuration ? moment.duration(snapDuration) : slotDuration;

    this.slotDuration = slotDuration;
    this.snapDuration = snapDuration;
    this.snapsPerSlot = slotDuration / snapDuration; // TODO: ensure an integer multiple?

    this.minResizeDuration = snapDuration; // hack

    this.minTime = moment.duration(view.opt('minTime'));
    this.maxTime = moment.duration(view.opt('maxTime'));

    // might be an array value (for TimelineView).
    // if so, getting the most granular entry (the last one probably).
    input = view.opt('slotLabelFormat');
    if ($.isArray(input)) {
      input = input[input.length - 1];
    }

    this.labelFormat = input || view.opt('smallTimeFormat'); // the computed default

    input = view.opt('slotLabelInterval');
    this.labelInterval = input ? moment.duration(input) : this.computeLabelInterval(slotDuration);
  },

  // Computes an automatic value for slotLabelInterval
  computeLabelInterval: function (slotDuration) {
    var i;
    var labelInterval;
    var slotsPerLabel;

    // find the smallest stock label interval that results in more than one slots-per-label
    for (i = AGENDA_STOCK_SUB_DURATIONS.length - 1; i >= 0; i--) {
      labelInterval = moment.duration(AGENDA_STOCK_SUB_DURATIONS[i]);
      slotsPerLabel = divideDurationByDuration(labelInterval, slotDuration);
      if (isInt(slotsPerLabel) && slotsPerLabel > 1) {
        return labelInterval;
      }
    }

    return moment.duration(slotDuration); // fall back. clone
  },

  // Computes a default event time formatting string if `timeFormat` is not explicitly defined
  computeEventTimeFormat: function () {
    return this.view.opt('noMeridiemTimeFormat'); // like "6:30" (no AM/PM)
  },

  // Computes a default `displayEventEnd` value if one is not expliclty defined
  computeDisplayEventEnd: function () {
    return true;
  },

  /* Hit System
	------------------------------------------------------------------------------------------------------------------*/

  prepareHits: function () {
    this.colCoordCache.build();
    this.slatCoordCache.build();
  },

  releaseHits: function () {
    this.colCoordCache.clear();
    // NOTE: don't clear slatCoordCache because we rely on it for computeTimeTop
  },

  queryHit: function (leftOffset, topOffset) {
    var snapsPerSlot = this.snapsPerSlot;
    var colCoordCache = this.colCoordCache;
    var slatCoordCache = this.slatCoordCache;

    if (colCoordCache.isLeftInBounds(leftOffset) && slatCoordCache.isTopInBounds(topOffset)) {
      var colIndex = colCoordCache.getHorizontalIndex(leftOffset);
      var slatIndex = slatCoordCache.getVerticalIndex(topOffset);

      if (colIndex != null && slatIndex != null) {
        var slatTop = slatCoordCache.getTopOffset(slatIndex);
        var slatHeight = slatCoordCache.getHeight(slatIndex);
        var partial = (topOffset - slatTop) / slatHeight; // floating point number between 0 and 1
        var localSnapIndex = Math.floor(partial * snapsPerSlot); // the snap # relative to start of slat
        var snapIndex = slatIndex * snapsPerSlot + localSnapIndex;
        var snapTop = slatTop + (localSnapIndex / snapsPerSlot) * slatHeight;
        var snapBottom = slatTop + ((localSnapIndex + 1) / snapsPerSlot) * slatHeight;

        return {
          col: colIndex,
          snap: snapIndex,
          component: this, // needed unfortunately :(
          left: colCoordCache.getLeftOffset(colIndex),
          right: colCoordCache.getRightOffset(colIndex),
          top: snapTop,
          bottom: snapBottom,
        };
      }
    }
  },

  getHitSpan: function (hit) {
    var start = this.getCellDate(0, hit.col); // row=0
    var time = this.computeSnapTime(hit.snap); // pass in the snap-index
    var end;

    start.time(time);
    end = start.clone().add(this.snapDuration);

    return {start: start, end: end};
  },

  getHitEl: function (hit) {
    return this.colEls.eq(hit.col);
  },

  /* Dates
	------------------------------------------------------------------------------------------------------------------*/

  rangeUpdated: function () {
    var date = this.start.clone();
    var dayIndex = -1;
    var dayIndices = [];
    var dayDates = [];
    var rowCnt;

    while (date.isBefore(this.end)) {
      // loop each day from start to end
      dayIndex++;
      dayIndices.push(dayIndex);
      dayDates.push(date.clone());
      date.add(1, 'days');
    }

    rowCnt = 1;
    daysPerRow = dayDates.length;

    this.dayDates = dayDates;
    this.dayIndices = dayIndices;
    this.daysPerRow = daysPerRow;
    this.rowCnt = rowCnt;
  },

  // Given a row number of the grid, representing a "snap", returns a time (Duration) from its start-of-day
  computeSnapTime: function (snapIndex) {
    return moment.duration(this.minTime + this.snapDuration * snapIndex);
  },

  /* Coordinates
	------------------------------------------------------------------------------------------------------------------*/

  updateSize: function (isResize) {
    // NOT a standard Grid method
    this.slatCoordCache.build();

    if (isResize) {
      this.updateSegVerticals([].concat(this.fgSegs || [], this.bgSegs || [], this.businessSegs || []));
    }
  },

  getTotalSlatHeight: function () {
    return this.slatContainerEl.outerHeight();
  },

  // Computes the top coordinate, relative to the bounds of the grid, of the given date.
  // A `startOfDayDate` must be given for avoiding ambiguity over how to treat midnight.
  computeDateTop: function (date, startOfDayDate) {
    return this.computeTimeTop(moment.duration(date - startOfDayDate.clone().stripTime()));
  },

  // Computes the top coordinate, relative to the bounds of the grid, of the given time (a Duration).
  computeTimeTop: function (time) {
    var len = this.slatEls.length;
    var slatCoverage = (time - this.minTime) / this.slotDuration; // floating-point value of # of slots covered
    var slatIndex;
    var slatRemainder;

    // compute a floating-point number for how many slats should be progressed through.
    // from 0 to number of slats (inclusive)
    // constrained because minTime/maxTime might be customized.
    slatCoverage = Math.max(0, slatCoverage);
    slatCoverage = Math.min(len, slatCoverage);

    // an integer index of the furthest whole slat
    // from 0 to number slats (*exclusive*, so len-1)
    slatIndex = Math.floor(slatCoverage);
    slatIndex = Math.min(slatIndex, len - 1);

    // how much further through the slatIndex slat (from 0.0-1.0) must be covered in addition.
    // could be 1.0 if slatCoverage is covering *all* the slots
    slatRemainder = slatCoverage - slatIndex;

    return this.slatCoordCache.getTopPosition(slatIndex) + this.slatCoordCache.getHeight(slatIndex) * slatRemainder;
  },

  /* Event Drag Visualization
	------------------------------------------------------------------------------------------------------------------*/

  // Renders a visual indication of an event being dragged over the specified date(s).
  // A returned value of `true` signals that a mock "helper" event has been rendered.
  renderDrag: function (eventLocation, seg) {
    if (seg) {
      // if there is event information for this drag, render a helper event

      // returns mock event elements
      // signal that a helper has been rendered
      return this.renderEventLocationHelper(eventLocation, seg);
    } else {
      // otherwise, just render a highlight
      this.renderHighlight(this.eventToSpan(eventLocation));
    }
  },

  // Unrenders any visual indication of an event being dragged
  unrenderDrag: function () {
    this.unrenderHelper();
    this.unrenderHighlight();
  },

  /* Event Resize Visualization
	------------------------------------------------------------------------------------------------------------------*/

  // Renders a visual indication of an event being resized
  renderEventResize: function (eventLocation, seg) {
    return this.renderEventLocationHelper(eventLocation, seg); // returns mock event elements
  },

  // Unrenders any visual indication of an event being resized
  unrenderEventResize: function () {
    this.unrenderHelper();
  },

  /* Event Helper
	------------------------------------------------------------------------------------------------------------------*/

  // Renders a mock "helper" event. `sourceSeg` is the original segment object and might be null (an external drag)
  renderHelper: function (event, sourceSeg) {
    return this.renderHelperSegs(this.eventToSegs(event), sourceSeg); // returns mock event elements
  },

  // Unrenders any mock helper event
  unrenderHelper: function () {
    this.unrenderHelperSegs();
  },

  /* Business Hours
	------------------------------------------------------------------------------------------------------------------*/

  renderBusinessHours: function () {
    this.renderBusinessSegs(this.buildBusinessHourSegs());
  },

  unrenderBusinessHours: function () {
    this.unrenderBusinessSegs();
  },

  /* Now Indicator
	------------------------------------------------------------------------------------------------------------------*/

  getNowIndicatorUnit: function () {
    return 'minute'; // will refresh on the minute
  },

  renderNowIndicator: function (date) {
    // seg system might be overkill, but it handles scenario where line needs to be rendered
    //  more than once because of columns with the same date (resources columns for example)
    var segs = this.spanToSegs({start: date, end: date});
    var top = this.computeDateTop(date, date);
    var nodes = [];
    var i;

    // render lines within the columns
    for (i = 0; i < segs.length; i++) {
      nodes.push(
        $('<div class="fc-now-indicator fc-now-indicator-line"></div>')
          .css('top', top)
          .appendTo(this.colContainerEls.eq(segs[i].col))[0]
      );
    }

    // render an arrow over the axis
    if (segs.length > 0) {
      // is the current time in view?
      nodes.push(
        $('<div class="fc-now-indicator fc-now-indicator-arrow"></div>')
          .css('top', top)
          .appendTo(this.el.find('.fc-content-skeleton'))[0]
      );
    }

    this.nowIndicatorEls = $(nodes);
  },

  unrenderNowIndicator: function () {
    if (this.nowIndicatorEls) {
      this.nowIndicatorEls.remove();
      this.nowIndicatorEls = null;
    }
  },

  /* Selection
	------------------------------------------------------------------------------------------------------------------*/

  // Renders a visual indication of a selection. Overrides the default, which was to simply render a highlight.
  renderSelection: function (span) {
    if (this.view.opt('selectHelper')) {
      // this setting signals that a mock helper event should be rendered

      // normally acceps an eventLocation, span has a start/end, which is good enough
      this.renderEventLocationHelper(span);
    } else {
      this.renderHighlight(span);
    }
  },

  // Unrenders any visual indication of a selection
  unrenderSelection: function () {
    this.unrenderHelper();
    this.unrenderHighlight();
  },

  /* Highlight
	------------------------------------------------------------------------------------------------------------------*/

  renderHighlight: function (span) {
    this.renderHighlightSegs(this.spanToSegs(span));
  },

  unrenderHighlight: function () {
    this.unrenderHighlightSegs();
  },
}));

/* Methods for rendering SEGMENTS, pieces of content that live on the view
 ( this file is no longer just for events )
----------------------------------------------------------------------------------------------------------------------*/

ResourceGrid.mixin({
  colContainerEls: null, // containers for each column

  // inner-containers for each column where different types of segs live
  fgContainerEls: null,
  bgContainerEls: null,
  helperContainerEls: null,
  highlightContainerEls: null,
  businessContainerEls: null,

  // arrays of different types of displayed segments
  fgSegs: null,
  bgSegs: null,
  helperSegs: null,
  highlightSegs: null,
  businessSegs: null,

  // Renders the DOM that the view's content will live in
  renderContentSkeleton: function () {
    var cellHtml = '';
    var i;
    var skeletonEl;

    for (i = 0; i < this.colCnt; i++) {
      cellHtml +=
        '<td>' +
        '<div class="fc-content-col">' +
        '<div class="fc-event-container fc-helper-container"></div>' +
        '<div class="fc-event-container"></div>' +
        '<div class="fc-highlight-container"></div>' +
        '<div class="fc-bgevent-container"></div>' +
        '<div class="fc-business-container"></div>' +
        '</div>' +
        '</td>';
    }

    skeletonEl = $('<div class="fc-content-skeleton">' + '<table>' + '<tr>' + cellHtml + '</tr>' + '</table>' + '</div>');

    this.colContainerEls = skeletonEl.find('.fc-content-col');
    this.helperContainerEls = skeletonEl.find('.fc-helper-container');
    this.fgContainerEls = skeletonEl.find('.fc-event-container:not(.fc-helper-container)');
    this.bgContainerEls = skeletonEl.find('.fc-bgevent-container');
    this.highlightContainerEls = skeletonEl.find('.fc-highlight-container');
    this.businessContainerEls = skeletonEl.find('.fc-business-container');

    this.bookendCells(skeletonEl.find('tr')); // TODO: do this on string level
    this.el.append(skeletonEl);
  },

  // Slices up the given span (unzoned start/end with other misc data) into an array of segments
  spanToSegs: function (span, event) {
    var segs;
    var i, seg;

    if (event) {
      segs = this.sliceRangeByRow(span);
      for (i = 0; i < segs.length; i++) {
        seg = segs[i];
        seg.start = event.start;
        seg.end = event.end;
        // col is determined by matching the resource
        if (event.resource) {
          var index;
          for (index = 0; index < this.resourceIds.length; index++) {
            if (event.resource == this.resourceIds[index]) {
              seg.col = index;
              break;
            }
          }
        }
        // remove events not matching a resource
        if (typeof seg.col == 'undefined') {
          segs.splice(i);
        }
      }
    } else {
      segs = [];
      var index;
      for (index = 0; index < this.colCnt; index++) {
        segs.push({
          start: span.start,
          end: span.end,
          col: index,
        });
      }
    }
    return segs;
  },

  // Given an event's span (unzoned start/end and other misc data), and the event itself,
  // slices into segments and attaches event-derived properties to them.
  eventSpanToSegs: function (span, event, segSliceFunc) {
    var segs = segSliceFunc ? segSliceFunc(span) : this.spanToSegs(span, event);
    var i, seg;

    for (i = 0; i < segs.length; i++) {
      seg = segs[i];
      seg.event = event;
      seg.eventStartMS = +span.start; // TODO: not the best name after making spans unzoned
      seg.eventDurationMS = span.end - span.start;
    }

    return segs;
  },

  /* Foreground Events
	------------------------------------------------------------------------------------------------------------------*/

  renderFgSegs: function (segs) {
    segs = this.renderFgSegsIntoContainers(segs, this.fgContainerEls);
    this.fgSegs = segs;
    return segs; // needed for Grid::renderEvents
  },

  unrenderFgSegs: function () {
    this.unrenderNamedSegs('fgSegs');
  },

  /* Foreground Helper Events
	------------------------------------------------------------------------------------------------------------------*/

  renderHelperSegs: function (segs, sourceSeg) {
    var helperEls = [];
    var i, seg;
    var sourceEl;

    segs = this.renderFgSegsIntoContainers(segs, this.helperContainerEls);

    // Try to make the segment that is in the same row as sourceSeg look the same
    for (i = 0; i < segs.length; i++) {
      seg = segs[i];
      if (sourceSeg && sourceSeg.col === seg.col) {
        sourceEl = sourceSeg.el;
        seg.el.css({
          left: sourceEl.css('left'),
          right: sourceEl.css('right'),
          'margin-left': sourceEl.css('margin-left'),
          'margin-right': sourceEl.css('margin-right'),
        });
      }
      helperEls.push(seg.el[0]);
    }

    this.helperSegs = segs;

    return $(helperEls); // must return rendered helpers
  },

  unrenderHelperSegs: function () {
    this.unrenderNamedSegs('helperSegs');
  },

  /* Background Events
	------------------------------------------------------------------------------------------------------------------*/

  renderBgSegs: function (segs) {
    segs = this.renderFillSegEls('bgEvent', segs); // TODO: old fill system
    this.updateSegVerticals(segs);
    this.attachSegsByCol(this.groupSegsByCol(segs), this.bgContainerEls);
    this.bgSegs = segs;
    return segs; // needed for Grid::renderEvents
  },

  unrenderBgSegs: function () {
    this.unrenderNamedSegs('bgSegs');
  },

  /* Highlight
	------------------------------------------------------------------------------------------------------------------*/

  renderHighlightSegs: function (segs) {
    segs = this.renderFillSegEls('highlight', segs); // TODO: old fill system
    this.updateSegVerticals(segs);
    this.attachSegsByCol(this.groupSegsByCol(segs), this.highlightContainerEls);
    this.highlightSegs = segs;
  },

  unrenderHighlightSegs: function () {
    this.unrenderNamedSegs('highlightSegs');
  },

  /* Business Hours
	------------------------------------------------------------------------------------------------------------------*/

  renderBusinessSegs: function (segs) {
    segs = this.renderFillSegEls('businessHours', segs); // TODO: old fill system
    this.updateSegVerticals(segs);
    this.attachSegsByCol(this.groupSegsByCol(segs), this.businessContainerEls);
    this.businessSegs = segs;
  },

  unrenderBusinessSegs: function () {
    this.unrenderNamedSegs('businessSegs');
  },

  /* Seg Rendering Utils
	------------------------------------------------------------------------------------------------------------------*/

  // Given a flat array of segments, return an array of sub-arrays, grouped by each segment's col
  groupSegsByCol: function (segs) {
    var segsByCol = [];
    var i;

    for (i = 0; i < this.colCnt; i++) {
      segsByCol.push([]);
    }

    for (i = 0; i < segs.length; i++) {
      segsByCol[segs[i].col].push(segs[i]);
    }

    return segsByCol;
  },

  // Given segments grouped by column, insert the segments' elements into a parallel array of container
  // elements, each living within a column.
  attachSegsByCol: function (segsByCol, containerEls) {
    var col;
    var segs;
    var i;

    for (col = 0; col < this.colCnt; col++) {
      // iterate each column grouping
      segs = segsByCol[col];

      for (i = 0; i < segs.length; i++) {
        containerEls.eq(col).append(segs[i].el);
      }
    }
  },

  // Given the name of a property of `this` object, assumed to be an array of segments,
  // loops through each segment and removes from DOM. Will null-out the property afterwards.
  unrenderNamedSegs: function (propName) {
    var segs = this[propName];
    var i;

    if (segs) {
      for (i = 0; i < segs.length; i++) {
        segs[i].el.remove();
      }
      this[propName] = null;
    }
  },

  /* Foreground Event Rendering Utils
	------------------------------------------------------------------------------------------------------------------*/

  // Given an array of foreground segments, render a DOM element for each, computes position,
  // and attaches to the column inner-container elements.
  renderFgSegsIntoContainers: function (segs, containerEls) {
    var segsByCol;
    var col;

    segs = this.renderFgSegEls(segs); // will call fgSegHtml
    segsByCol = this.groupSegsByCol(segs);

    for (col = 0; col < this.colCnt; col++) {
      this.updateFgSegCoords(segsByCol[col]);
    }

    this.attachSegsByCol(segsByCol, containerEls);

    return segs;
  },

  // Renders the HTML for a single event segment's default rendering
  fgSegHtml: function (seg, disableResizing) {
    var view = this.view;
    var event = seg.event;
    var isDraggable = view.isEventDraggable(event);
    var isResizableFromStart = !disableResizing && seg.isStart && view.isEventResizableFromStart(event);
    var isResizableFromEnd = !disableResizing && seg.isEnd && view.isEventResizableFromEnd(event);
    var classes = this.getSegClasses(seg, isDraggable, isResizableFromStart || isResizableFromEnd);
    var skinCss = cssToStr(this.getSegSkinCss(seg));
    var timeText;
    var fullTimeText; // more verbose time text. for the print stylesheet
    var startTimeText; // just the start time text

    classes.unshift('fc-time-grid-event', 'fc-v-event');

    if (view.isMultiDayEvent(event)) {
      // if the event appears to span more than one day...
      // Don't display time text on segments that run entirely through a day.
      // That would appear as midnight-midnight and would look dumb.
      // Otherwise, display the time text for the *segment's* times (like 6pm-midnight or midnight-10am)
      if (seg.isStart || seg.isEnd) {
        timeText = this.getEventTimeText(seg);
        fullTimeText = this.getEventTimeText(seg, 'LT');
        startTimeText = this.getEventTimeText(seg, null, false); // displayEnd=false
      }
    } else {
      // Display the normal time text for the *event's* times
      timeText = this.getEventTimeText(event);
      fullTimeText = this.getEventTimeText(event, 'LT');
      startTimeText = this.getEventTimeText(event, null, false); // displayEnd=false
    }

    return (
      '<a class="' +
      classes.join(' ') +
      '"' +
      (event.url ? ' href="' + htmlEscape(event.url) + '"' : '') +
      (skinCss ? ' style="' + skinCss + '"' : '') +
      '>' +
      '<div class="fc-content">' +
      (timeText
        ? '<div class="fc-time"' +
          ' data-start="' +
          htmlEscape(startTimeText) +
          '"' +
          ' data-full="' +
          htmlEscape(fullTimeText) +
          '"' +
          '>' +
          '<span>' +
          htmlEscape(timeText) +
          '</span>' +
          '</div>'
        : '') +
      (event.title ? '<div class="fc-title">' + htmlEscape(event.title) + '</div>' : '') +
      '</div>' +
      '<div class="fc-bg"/>' +
      /* TODO: write CSS for this
			(isResizableFromStart ?
				'<div class="fc-resizer fc-start-resizer" />' :
				''
				) +
			*/
      (isResizableFromEnd ? '<div class="fc-resizer fc-end-resizer" />' : '') +
      '</a>'
    );
  },

  /* Seg Position Utils
	------------------------------------------------------------------------------------------------------------------*/

  // Refreshes the CSS top/bottom coordinates for each segment element.
  // Works when called after initial render, after a window resize/zoom for example.
  updateSegVerticals: function (segs) {
    this.computeSegVerticals(segs);
    this.assignSegVerticals(segs);
  },

  // For each segment in an array, computes and assigns its top and bottom properties
  computeSegVerticals: function (segs) {
    var i, seg;

    for (i = 0; i < segs.length; i++) {
      seg = segs[i];
      seg.top = this.computeDateTop(seg.start, seg.start);
      seg.bottom = this.computeDateTop(seg.end, seg.start);
    }
  },

  // Given segments that already have their top/bottom properties computed, applies those values to
  // the segments' elements.
  assignSegVerticals: function (segs) {
    var i, seg;

    for (i = 0; i < segs.length; i++) {
      seg = segs[i];
      seg.el.css(this.generateSegVerticalCss(seg));
    }
  },

  // Generates an object with CSS properties for the top/bottom coordinates of a segment element
  generateSegVerticalCss: function (seg) {
    return {
      top: seg.top,
      bottom: -seg.bottom, // flipped because needs to be space beyond bottom edge of event container
    };
  },

  /* Foreground Event Positioning Utils
	------------------------------------------------------------------------------------------------------------------*/

  // Given segments that are assumed to all live in the *same column*,
  // compute their verical/horizontal coordinates and assign to their elements.
  updateFgSegCoords: function (segs) {
    this.computeSegVerticals(segs); // horizontals relies on this
    this.computeFgSegHorizontals(segs); // compute horizontal coordinates, z-index's, and reorder the array
    this.assignSegVerticals(segs);
    this.assignFgSegHorizontals(segs);
  },

  // Given an array of segments that are all in the same column, sets the backwardCoord and forwardCoord on each.
  // NOTE: Also reorders the given array by date!
  computeFgSegHorizontals: function (segs) {
    var levels;
    var level0;
    var i;

    this.sortEventSegs(segs); // order by certain criteria
    levels = buildSlotSegLevels(segs);
    computeForwardSlotSegs(levels);

    if ((level0 = levels[0])) {
      for (i = 0; i < level0.length; i++) {
        computeSlotSegPressures(level0[i]);
      }

      for (i = 0; i < level0.length; i++) {
        this.computeFgSegForwardBack(level0[i], 0, 0);
      }
    }
  },

  // Calculate seg.forwardCoord and seg.backwardCoord for the segment, where both values range
  // from 0 to 1. If the calendar is left-to-right, the seg.backwardCoord maps to "left" and
  // seg.forwardCoord maps to "right" (via percentage). Vice-versa if the calendar is right-to-left.
  //
  // The segment might be part of a "series", which means consecutive segments with the same pressure
  // who's width is unknown until an edge has been hit. `seriesBackwardPressure` is the number of
  // segments behind this one in the current series, and `seriesBackwardCoord` is the starting
  // coordinate of the first segment in the series.
  computeFgSegForwardBack: function (seg, seriesBackwardPressure, seriesBackwardCoord) {
    var forwardSegs = seg.forwardSegs;
    var i;

    if (seg.forwardCoord === undefined) {
      // not already computed

      if (!forwardSegs.length) {
        // if there are no forward segments, this segment should butt up against the edge
        seg.forwardCoord = 1;
      } else {
        // sort highest pressure first
        this.sortForwardSegs(forwardSegs);

        // this segment's forwardCoord will be calculated from the backwardCoord of the
        // highest-pressure forward segment.
        this.computeFgSegForwardBack(forwardSegs[0], seriesBackwardPressure + 1, seriesBackwardCoord);
        seg.forwardCoord = forwardSegs[0].backwardCoord;
      }

      // calculate the backwardCoord from the forwardCoord. consider the series
      seg.backwardCoord =
        seg.forwardCoord -
        (seg.forwardCoord - seriesBackwardCoord) / // available width for series
          (seriesBackwardPressure + 1); // # of segments in the series

      // use this segment's coordinates to computed the coordinates of the less-pressurized
      // forward segments
      for (i = 0; i < forwardSegs.length; i++) {
        this.computeFgSegForwardBack(forwardSegs[i], 0, seg.forwardCoord);
      }
    }
  },

  sortForwardSegs: function (forwardSegs) {
    forwardSegs.sort(proxy(this, 'compareForwardSegs'));
  },

  // A cmp function for determining which forward segment to rely on more when computing coordinates.
  compareForwardSegs: function (seg1, seg2) {
    // put higher-pressure first
    return (
      seg2.forwardPressure - seg1.forwardPressure ||
      // put segments that are closer to initial edge first (and favor ones with no coords yet)
      (seg1.backwardCoord || 0) - (seg2.backwardCoord || 0) ||
      // do normal sorting...
      this.compareEventSegs(seg1, seg2)
    );
  },

  // Given foreground event segments that have already had their position coordinates computed,
  // assigns position-related CSS values to their elements.
  assignFgSegHorizontals: function (segs) {
    var i, seg;

    for (i = 0; i < segs.length; i++) {
      seg = segs[i];
      seg.el.css(this.generateFgSegHorizontalCss(seg));

      // if the height is short, add a className for alternate styling
      if (seg.bottom - seg.top < 30) {
        seg.el.addClass('fc-short');
      }
    }
  },

  // Generates an object with CSS properties/values that should be applied to an event segment element.
  // Contains important positioning-related properties that should be applied to any event element, customized or not.
  generateFgSegHorizontalCss: function (seg) {
    var shouldOverlap = this.view.opt('slotEventOverlap');
    var backwardCoord = seg.backwardCoord; // the left side if LTR. the right side if RTL. floating-point
    var forwardCoord = seg.forwardCoord; // the right side if LTR. the left side if RTL. floating-point
    var props = this.generateSegVerticalCss(seg); // get top/bottom first
    var left; // amount of space from left edge, a fraction of the total width
    var right; // amount of space from right edge, a fraction of the total width

    if (shouldOverlap) {
      // double the width, but don't go beyond the maximum forward coordinate (1.0)
      forwardCoord = Math.min(1, backwardCoord + (forwardCoord - backwardCoord) * 2);
    }

    if (this.isRTL) {
      left = 1 - forwardCoord;
      right = backwardCoord;
    } else {
      left = backwardCoord;
      right = 1 - forwardCoord;
    }

    props.zIndex = seg.level + 1; // convert from 0-base to 1-based
    props.left = left * 100 + '%';
    props.right = right * 100 + '%';

    if (shouldOverlap && seg.forwardPressure) {
      // add padding to the edge so that forward stacked events don't cover the resizer's icon
      props[this.isRTL ? 'marginLeft' : 'marginRight'] = 10 * 2; // 10 is a guesstimate of the icon's width
    }

    return props;
  },

  // Computes the ambiguously-timed moment for the given cell
  getCellDate: function (row, col) {
    return this.dayDates[0].clone();
  },
});

// Builds an array of segments "levels". The first level will be the leftmost tier of segments if the calendar is
// left-to-right, or the rightmost if the calendar is right-to-left. Assumes the segments are already ordered by date.
function buildSlotSegLevels(segs) {
  var levels = [];
  var i, seg;
  var j;

  for (i = 0; i < segs.length; i++) {
    seg = segs[i];

    // go through all the levels and stop on the first level where there are no collisions
    for (j = 0; j < levels.length; j++) {
      if (!computeSlotSegCollisions(seg, levels[j]).length) {
        break;
      }
    }

    seg.level = j;

    (levels[j] || (levels[j] = [])).push(seg);
  }

  return levels;
}

// For every segment, figure out the other segments that are in subsequent
// levels that also occupy the same vertical space. Accumulate in seg.forwardSegs
function computeForwardSlotSegs(levels) {
  var i, level;
  var j, seg;
  var k;

  for (i = 0; i < levels.length; i++) {
    level = levels[i];

    for (j = 0; j < level.length; j++) {
      seg = level[j];

      seg.forwardSegs = [];
      for (k = i + 1; k < levels.length; k++) {
        computeSlotSegCollisions(seg, levels[k], seg.forwardSegs);
      }
    }
  }
}

// Figure out which path forward (via seg.forwardSegs) results in the longest path until
// the furthest edge is reached. The number of segments in this path will be seg.forwardPressure
function computeSlotSegPressures(seg) {
  var forwardSegs = seg.forwardSegs;
  var forwardPressure = 0;
  var i, forwardSeg;

  if (seg.forwardPressure === undefined) {
    // not already computed

    for (i = 0; i < forwardSegs.length; i++) {
      forwardSeg = forwardSegs[i];

      // figure out the child's maximum forward path
      computeSlotSegPressures(forwardSeg);

      // either use the existing maximum, or use the child's forward pressure
      // plus one (for the forwardSeg itself)
      forwardPressure = Math.max(forwardPressure, 1 + forwardSeg.forwardPressure);
    }

    seg.forwardPressure = forwardPressure;
  }
}

// Find all the segments in `otherSegs` that vertically collide with `seg`.
// Append into an optionally-supplied `results` array and return.
function computeSlotSegCollisions(seg, otherSegs, results) {
  results = results || [];

  for (var i = 0; i < otherSegs.length; i++) {
    if (isSlotSegCollision(seg, otherSegs[i])) {
      results.push(otherSegs[i]);
    }
  }

  return results;
}

// Do these segments occupy the same vertical space?
function isSlotSegCollision(seg1, seg2) {
  return seg1.bottom > seg2.top && seg1.top < seg2.bottom;
}
