-- Data processing based on openmaptiles.org schema
-- https://openmaptiles.org/schema/
-- Copyright (c) 2016, KlokanTech.com & OpenMapTiles contributors.
-- Used under CC-BY 4.0

--------
-- Alter these lines to control which languages are written for place/streetnames
--
-- Preferred language can be (for example) "en" for English, "de" for German, or nil to use OSM's name tag:
preferred_language = "de"
-- This is written into the following vector tile attribute (usually "name:latin"):
preferred_language_attribute = "name:latin"
-- If OSM's name tag differs, then write it into this attribute (usually "name_int"):
default_language_attribute = "name_int"
-- Also write these languages if they differ - for example, { "de", "fr" }
additional_languages = { "en" }
--------

-- Enter/exit Tilemaker
function init_function()
end
function exit_function()
end

-- Implement Sets in tables
function Set(list)
	local set = {}
	for _, l in ipairs(list) do set[l] = true end
	return set
end

-- Meters per pixel if tile is 256x256
ZRES5  = 4891.97
ZRES6  = 2445.98
ZRES7  = 1222.99
ZRES8  = 611.5
ZRES9  = 305.7
ZRES10 = 152.9
ZRES11 = 76.4
ZRES12 = 38.2
ZRES13 = 19.1

-- Nodes will only be processed if one of these keys is present

node_keys = { "amenity", "shop", "barrier","highway","historic", "railway", "natural" }

-- Initialize Lua logic

function init_function()
end

-- Finalize Lua logic()
function exit_function()
end

-- Assign nodes to a layer, and set attributes, based on OSM tags

function node_function(node)
	local amenity = node:Find("amenity")
	local shop = node:Find("shop")
	if amenity~="" or shop~="" then
		node:Layer("poi", false)
		if amenity~="" then node:Attribute("class",amenity)
		else node:Attribute("class",shop) end
		node:Attribute("name", node:Find("name"))
	end
end

-- Similarly for ways

function way_function(way)
	local highway = way:Find("highway")
	local waterway = way:Find("waterway")
	local building = way:Find("building")
	if highway~="" then
		way:Layer("transportation", false)
		way:Attribute("class", highway)
--		way:Attribute("id",way:Id())
--		way:AttributeNumeric("area",37)
	end
	if waterway~="" then
		way:Layer("waterway", false)
		way:Attribute("class", waterway)
	end
	if building~="" then
		way:Layer("building", true)
	end
end
